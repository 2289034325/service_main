package com.acxca.ava.api.app;

import com.acxca.ava.config.Properties;
import com.acxca.ava.entity.*;
import com.acxca.ava.repository.SpeechRepository;
import com.acxca.components.java.util.AliyunOSSClient;
import com.acxca.components.spring.jwt.JwtUserDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "app/speech",produces= MediaType.APPLICATION_JSON_VALUE)
public class AppSpeechController {
    @Autowired
    private SpeechRepository speechRepository;

    @Autowired
    private AliyunOSSClient ossUtil;

    @Autowired
    private Properties properties;

    @Autowired
    private RestTemplate restTemplate;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(path = "article/list", method = RequestMethod.GET)
    public ResponseEntity<Object> searchArticles(@RequestParam(value = "lang", required = false) Integer lang, @RequestParam(value = "title", required = false) String title) {

        // 让审核账号只能看到一个
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(ud.getName().equals("guest")){
            title = "Wolf of Wall Street";
        }

        Iterable<Article> articles = speechRepository.selectArticles(lang, title);

        return new ResponseEntity(articles, HttpStatus.OK);
    }

    @RequestMapping(path = "article/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getArticle(@PathVariable("id") String id) {

        Article article = speechRepository.selectArticleById(id);
        List<Paragraph> paragraphs = speechRepository.selectAllParagraphsByArticleId(id);
        List<ParagraphSplit> splits = speechRepository.selectSplitsByArticleId(id);
        List<Media> medias = speechRepository.selectMediasByArticleId(id);
        medias.forEach(m->{
            String mediaUrl = ossUtil.getSignedUrl(properties.getOssBucket(), m.getPath() + "/" + m.getName());
            m.setUrl(mediaUrl);
        });

        paragraphs.stream().forEach(p -> {
            p.setSplits(splits.stream().filter(s -> s.getParagraph_id().equals(p.getId())).collect(Collectors.toList()));
        });
        article.setParagraphs(paragraphs);
        article.setMedias(medias);

        return new ResponseEntity(article, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite/{split_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReciteHistory(@PathVariable("split_id") String id) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ReciteRecord> records = speechRepository.selectSplitReciteHistory(ud.getId(),id);

        return new ResponseEntity(records, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite", method = RequestMethod.POST)
    public ResponseEntity<Object> saveReciteHistory(@RequestBody ReciteRecord record) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        record.setUser_id(ud.getId());
        record.setSubmit_time(new Date());

        speechRepository.insertSplitReciteRecord(record);

        return new ResponseEntity(record, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite", method = RequestMethod.PUT)
    public ResponseEntity<Object> setReciteScore(@RequestBody Map param) {
        String id = param.get("id").toString();
        float score = Integer.parseInt(param.get("score").toString());

        speechRepository.updateReciteScore(id,score);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/media/{id}", method = RequestMethod.GET)
    public void getMedia(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {

        Media m = speechRepository.selectMedia(id);

        // 先看本地有没有，没有就从oss下载，保存到服务端后再返回给客户端
        File file = null;
        try {
            String path = Paths.get(properties.getMediaLocalPath(), m.getName()).toString();
            file = new File(path);
            //确保目录存在
            file.getParentFile().mkdirs();

            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                ossUtil.downLoadOssFile(fos, properties.getOssBucket(), properties.getOssMediaFolder() + "/" + m.getName());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        //下载开始位置
        long startByte = 0;
        //下载结束位置
        long endByte = file.length() - 1;

        //获取下载范围
        String range = request.getHeader("range");
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String rangeArray[] = range.split("-");
            if (rangeArray.length == 1) {
                //Example: bytes=1024-
                if (range.endsWith("-")) {
                    startByte = Long.parseLong(rangeArray[0]);
                } else { //Example: bytes=-1024
                    endByte = Long.parseLong(rangeArray[0]);
                }
            }
            //Example: bytes=2048-4096
            else if (rangeArray.length == 2) {
                startByte = Long.parseLong(rangeArray[0]);
                endByte = Long.parseLong(rangeArray[1]);
            }
        }

        long contentLength = endByte - startByte + 1;
        String contentType = request.getServletContext().getMimeType(m.getName());

        //HTTP 响应头设置
        //断点续传，HTTP 状态码必须为 206，否则不设置，如果非断点续传设置 206 状态码，则浏览器无法下载
        if (range != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        response.setContentType(contentType);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Accept-Ranges", "bytes");
        //Content-Range: 下载开始位置-下载结束位置/文件大小
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        //Content-disposition: inline; filename=xxx.xxx 表示浏览器内嵌显示该文件
        //Content-disposition: attachment; filename=xxx.xxx 表示浏览器下载该文件
        response.setHeader("Content-Disposition", "inline; filename=\"" + m.getName()+"\"");

        //传输文件流
        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmittedLength = 0;
        try {
            //以只读模式设置文件指针偏移量
            randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(startByte);

            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[4096];
            int len;
            while (transmittedLength < contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmittedLength += len;
            }

            outputStream.flush();
            response.flushBuffer();
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
