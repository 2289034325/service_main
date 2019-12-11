package com.acxca.ava.api.console;

import com.acxca.ava.config.Properties;
import com.acxca.ava.consts.CustomMessageMap;
import com.acxca.ava.consts.MediaUsage;
import com.acxca.ava.entity.Article;
import com.acxca.ava.entity.Media;
import com.acxca.ava.entity.Paragraph;
import com.acxca.ava.entity.ParagraphSplit;
import com.acxca.ava.repository.SpeechRepository;
import com.acxca.components.java.consts.BusinessMessageMap;
import com.acxca.components.java.entity.BusinessException;
import com.acxca.components.java.util.AliyunOSSClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "console/speech", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsoleSpeechController {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SpeechRepository speechRepository;

    @Autowired
    private AliyunOSSClient ossUtil;

    @Autowired
    private Properties properties;

    @RequestMapping(path = "article/list", method = RequestMethod.GET)
    public ResponseEntity<Object> searchArticles(@RequestParam(value = "lang", required = false) Integer lang, @RequestParam(value = "title", required = false) String title) {

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

    @RequestMapping(path = "article/modify", method = RequestMethod.PUT)
    public ResponseEntity<Object> modifyArticle(@RequestBody Article article) {

        speechRepository.updateArticle(article);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteArticle(@PathVariable String id) {

        speechRepository.deleteArticle(id);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/add", method = RequestMethod.POST)
    public ResponseEntity<Object> addArticle(@RequestBody Article article) {

        article.setId(UUID.randomUUID().toString());
        speechRepository.insertArticle(article);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/{articleId}/paragraphs", method = RequestMethod.GET)
    public ResponseEntity<Object> getParagraphs(@PathVariable("articleId") String articleId) {

        Iterable<Paragraph> paragraphs = speechRepository.selectAllParagraphsByArticleId(articleId);

        return new ResponseEntity(paragraphs, HttpStatus.OK);
    }

    /**
     *
     * @param file
     * @param article_id
     * @param media_usage 可以是原声video,audio，可以是讲解视频，可以是模仿视频音频
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "media/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("article_id") String article_id,
                                             @RequestParam("media_usage") int media_usage) throws Exception {

        Article article = speechRepository.selectArticleById(article_id);

        //以输入流的形式上传文件
        InputStream is = file.getInputStream();
        //文件名
        String originalFilename = file.getOriginalFilename();
        String fileNameSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        MediaUsage mu = MediaUsage.fromValue(media_usage);
        if(mu == null){
            throw new BusinessException(CustomMessageMap.REQUEST_PARAM_INVALID);
        }
        String fileName = article.getPerformer() + "-" + article.getTitle() + "-" + mu.getName() + "." + fileNameSuffix;
        //文件大小
        Long fileSize = file.getSize();

        ossUtil.uploadObject2OSS(is, fileName, fileSize, properties.getOssBucket(), properties.getOssMediaFolder());

        String path = properties.getOssMediaFolder();
        //时长暂时不解析 TODO
        float time = 0;

        Media md = new Media();
        md.setId(UUID.randomUUID().toString());
        md.setName(fileName);
        md.setUsage(media_usage);
        md.setPath(path);
        md.setTime(time);

        speechRepository.insertMedia(md);
        speechRepository.insertArticleMediaR(UUID.randomUUID().toString(),article_id,md.getId(),media_usage);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> addParagraphs(@RequestBody List<Paragraph> paragraphs) throws Exception {

        //循环插入段落和分段
        paragraphs.forEach(p -> {
            p.setId(UUID.randomUUID().toString());
            speechRepository.insertParagraph(p);

            //whole paragraph as a split
            ParagraphSplit split = new ParagraphSplit();
            split.setArticle_id(p.getArticle_id());
            split.setParagraph_id(p.getId());
            split.setId(UUID.randomUUID().toString());

            split.setStart_index(0);
            split.setEnd_index(p.getText().length() - 1);
            split.setStart_time(0);
            split.setEnd_time(0);
            speechRepository.insertSplit(split);
        });

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/modify", method = RequestMethod.POST)
    public ResponseEntity<Object> modifyParagraph(@RequestBody Paragraph paragraph) {

        speechRepository.updateParagraph(paragraph);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteParagraph(@PathVariable String id) {

        speechRepository.deleteParagraph(id);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/split", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> splitParagraph(@RequestBody Paragraph paragraph) {

        speechRepository.deleteSplits(paragraph.getId());
        for(ParagraphSplit ps: paragraph.getSplits()){
            ps.setId(UUID.randomUUID().toString());
        }
        speechRepository.insertSplits(paragraph.getSplits());

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/split/settime", method = RequestMethod.POST)
    public ResponseEntity<Object> setSplitTime(@RequestBody ParagraphSplit split) {

        speechRepository.updateSplitTime(split);

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
