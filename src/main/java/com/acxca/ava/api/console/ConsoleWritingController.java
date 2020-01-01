package com.acxca.ava.api.console;

import com.acxca.ava.entity.Paragraph;
import com.acxca.ava.entity.ParagraphSplit;
import com.acxca.ava.entity.WritingArticle;
import com.acxca.ava.repository.WritingRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "console/writing", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsoleWritingController {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private WritingRepository writingRepository;

    @RequestMapping(path = "article/list", method = RequestMethod.GET)
    public ResponseEntity<Object> searchArticles(@RequestParam(value = "lang", required = false) Integer lang, @RequestParam(value = "title", required = false) String title) {

        Iterable<WritingArticle> articles = writingRepository.selectArticles(lang, title);

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

    @RequestMapping(path = "article/modify", method = RequestMethod.PUT)
    public ResponseEntity<Object> modifyArticle(@RequestBody WritingArticle article) {

        writingRepository.updateArticle(article);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteArticle(@PathVariable String id) {

        writingRepository.deleteArticle(id);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/add", method = RequestMethod.POST)
    public ResponseEntity<Object> addArticle(@RequestBody WritingArticle article) {

        article.setId(UUID.randomUUID().toString());
        article.setInsert_time(new Date());
        writingRepository.insertArticle(article);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "article/{articleId}/paragraphs", method = RequestMethod.GET)
    public ResponseEntity<Object> getParagraphs(@PathVariable("articleId") String articleId) {

        Iterable<Paragraph> paragraphs = writingRepository.selectAllParagraphsByArticleId(articleId);

        return new ResponseEntity(paragraphs, HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/add", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> addParagraphs(@RequestBody List<Paragraph> paragraphs) {

        //循环插入段落和分段
        paragraphs.forEach(p -> {
            p.setId(UUID.randomUUID().toString());
            writingRepository.insertParagraph(p);

            //whole paragraph as a split
            ParagraphSplit split = new ParagraphSplit();
            split.setArticle_id(p.getArticle_id());
            split.setParagraph_id(p.getId());
            split.setId(UUID.randomUUID().toString());

            split.setStart_index(0);
            split.setEnd_index(p.getText().length() - 1);
            writingRepository.insertSplit(split);
        });

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/modify", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> modifyParagraph(@RequestBody Paragraph paragraph) {

        Paragraph op = writingRepository.selectParagraphById(paragraph.getId());
        // 删掉原有的分段，插入一个新的
        writingRepository.deleteSplits(paragraph.getId());
        writingRepository.deleteReciteRecord(paragraph.getId());

        ParagraphSplit split = new ParagraphSplit();
        split.setArticle_id(op.getArticle_id());
        split.setParagraph_id(op.getId());
        split.setId(UUID.randomUUID().toString());
        split.setStart_index(0);
        split.setEnd_index(paragraph.getText().length() - 1);
        writingRepository.insertSplit(split);

        writingRepository.updateParagraph(paragraph);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteParagraph(@PathVariable String id) {

        writingRepository.deleteParagraph(id);
        writingRepository.deleteReciteRecord(id);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "paragraph/split", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> splitParagraph(@RequestBody Paragraph paragraph) {

        writingRepository.deleteSplits(paragraph.getId());
        writingRepository.deleteReciteRecord(paragraph.getId());

        paragraph.getSplits().forEach(s->s.setId(UUID.randomUUID().toString()));
        writingRepository.insertSplits(paragraph.getSplits());

        return new ResponseEntity("", HttpStatus.OK);
    }
}
