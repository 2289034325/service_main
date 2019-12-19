package com.acxca.ava.api.app;

import com.acxca.ava.entity.Paragraph;
import com.acxca.ava.entity.ParagraphSplit;
import com.acxca.ava.entity.ReciteRecord;
import com.acxca.ava.entity.WritingArticle;
import com.acxca.ava.repository.WritingRepository;
import com.acxca.components.spring.jwt.JwtUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "app/writing",produces= MediaType.APPLICATION_JSON_VALUE)
public class AppWritingController {
    @Autowired
    private WritingRepository writingRepository;

    @RequestMapping(path = "article/list", method = RequestMethod.GET)
    public ResponseEntity<Object> searchArticles(@RequestParam(value = "lang", required = false) Integer lang, @RequestParam(value = "title", required = false) String title) {

        List<WritingArticle> articles = writingRepository.selectArticles(lang, title);

        return new ResponseEntity(articles, HttpStatus.OK);
    }

    @RequestMapping(path = "article/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getArticle(@PathVariable("id") String id) {

        WritingArticle article = writingRepository.selectArticleById(id);
        List<Paragraph> paragraphs = writingRepository.selectAllParagraphsByArticleId(id);
        List<ParagraphSplit> splits = writingRepository.selectSplitsByArticleId(id);

        paragraphs.stream().forEach(p -> {
            p.setSplits(splits.stream().filter(s -> s.getParagraph_id().equals(p.getId())).collect(Collectors.toList()));
        });
        article.setParagraphs(paragraphs);

        return new ResponseEntity(article, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite/{split_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReciteHistory(@PathVariable("split_id") String id) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ReciteRecord> records = writingRepository.selectSplitReciteHistory(ud.getId(),id);

        return new ResponseEntity(records, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite", method = RequestMethod.POST)
    public ResponseEntity<Object> saveReciteHistory(@RequestBody ReciteRecord record) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        record.setUser_id(ud.getId());
        record.setSubmit_time(new Date());

        writingRepository.insertSplitReciteRecord(record);

        return new ResponseEntity(record, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite", method = RequestMethod.PUT)
    public ResponseEntity<Object> setReciteScore(@RequestBody Map param) {
        String id = param.get("id").toString();
        float score = Integer.parseInt(param.get("score").toString());

        writingRepository.updateReciteScore(id,score);

        return new ResponseEntity("", HttpStatus.OK);
    }
}
