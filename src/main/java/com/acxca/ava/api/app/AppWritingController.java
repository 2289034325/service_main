package com.acxca.ava.api.app;

import com.acxca.ava.entity.Paragraph;
import com.acxca.ava.entity.ParagraphSplit;
import com.acxca.ava.entity.ReciteRecord;
import com.acxca.ava.entity.WritingArticle;
import com.acxca.ava.repository.WritingRepository;
import com.acxca.components.java.util.DiffUtil;
import com.acxca.components.java.util.diff_match_patch;
import com.acxca.components.spring.jwt.JwtUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        WritingArticle article = writingRepository.selectArticleById(id);
        List<Paragraph> paragraphs = writingRepository.selectAllParagraphsByArticleId(id);
        List<ParagraphSplit> splits = writingRepository.selectSplitsByArticleId(id);
        List<ReciteRecord> histories = writingRepository.selectArticleReciteHistory(ud.getId(),id);

        // 取每个split的最新默写记录
        List<ReciteRecord> nhs = new ArrayList<>();
        for(ParagraphSplit sp: splits){
            Optional<ReciteRecord> rr = histories.stream().filter(h->h.getSplit_id().equalsIgnoreCase(sp.getId())).sorted((h1,h2)->h1.getSubmit_time().after(h2.getSubmit_time())?-1:1).findFirst();
            if(rr.isPresent()){
                ReciteRecord nrr = rr.get();
                // 找到其所属的段落和片段
                Optional<Paragraph> prg = paragraphs.stream().filter(p->p.getId().equalsIgnoreCase(nrr.getParagraph_id())).findFirst();
                Optional<ParagraphSplit> spl = splits.stream().filter(s->s.getId().equalsIgnoreCase(nrr.getSplit_id())).findFirst();

                String originText = prg.get().getText().substring(spl.get().getStart_index(),spl.get().getEnd_index()+1);

                List<diff_match_patch.Diff> dfs = DiffUtil.diff_by_word(originText,nrr.getContent(),true);
                nrr.setDiffs(dfs);

                nhs.add(nrr);
            }
        }

//        for(ReciteRecord rr:histories){
//            // 找到其所属的段落和片段
//            Optional<Paragraph> prg = paragraphs.stream().filter(p->p.getId().equalsIgnoreCase(rr.getParagraph_id())).findFirst();
//            Optional<ParagraphSplit> spl = splits.stream().filter(s->s.getId().equalsIgnoreCase(rr.getSplit_id())).findFirst();
//
//            String originText = prg.get().getText().substring(spl.get().getStart_index(),spl.get().getEnd_index()+1);
//
//            List<diff_match_patch.Diff> dfs = DiffUtil.diff_by_word(originText,rr.getContent(),true);
//            rr.setDiffs(dfs);
//        }

        paragraphs.stream().forEach(p -> {
            p.setSplits(splits.stream().filter(s -> s.getParagraph_id().equals(p.getId())).collect(Collectors.toList()));
        });
        article.setParagraphs(paragraphs);
        article.setHistories(nhs);

        return new ResponseEntity(article, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite/{split_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReciteHistory(@PathVariable("split_id") String id) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ParagraphSplit ps = writingRepository.selectSplitById(id);
        Paragraph p = writingRepository.selectParagraphById(ps.getParagraph_id());
        String originText = p.getText().substring(ps.getStart_index(),ps.getEnd_index()+1);
        List<ReciteRecord> records = writingRepository.selectSplitReciteHistory(ud.getId(),id);
        for(ReciteRecord rr:records){
            List<diff_match_patch.Diff> dfs = DiffUtil.diff_by_word(originText,rr.getContent(),true);
            rr.setDiffs(dfs);
        }

        return new ResponseEntity(records, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite", method = RequestMethod.POST)
    public ResponseEntity<Object> saveReciteHistory(@RequestBody ReciteRecord record) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ParagraphSplit ps = writingRepository.selectSplitById(record.getSplit_id());
        Paragraph p = writingRepository.selectParagraphById(ps.getParagraph_id());
        String originText = p.getText().substring(ps.getStart_index(),ps.getEnd_index()+1);

        // 计算得分
        // 错一个单词扣0.5分，标点符号不计，大小写不计，总分5分，扣完为止
        float score = 5.0f;
        List<diff_match_patch.Diff> dfs = DiffUtil.diff_by_word(originText,record.getContent(),true);
        for(diff_match_patch.Diff df:dfs){
            if(df.operation == diff_match_patch.Operation.DELETE){
                String[] words = df.text.split(" ");
                for(String w: words){
                    // 判断是否是单词（有字母，即判定为单词）
                    if(w.matches(".*[a-zA-Z]+.*")) {
                        score -= 0.5;
                    }
                }
            }
            if(score <= 0){
                score = 0;
                break;
            }
        }

        record.setUser_id(ud.getId());
        record.setSubmit_time(new Date());
        record.setScore(score);
        writingRepository.insertSplitReciteRecord(record);

        // 将dif结果返回前台，用于页面渲染
        record.setDiffs(dfs);

        return new ResponseEntity(record, HttpStatus.OK);
    }

    @RequestMapping(path = "article/recite", method = RequestMethod.PUT)
    public ResponseEntity<Object> setReciteScore(@RequestBody Map param) {
        String id = param.get("id").toString();
        float score = Float.parseFloat(param.get("score").toString());

        writingRepository.updateReciteScore(id,score);

        return new ResponseEntity("", HttpStatus.OK);
    }
}
