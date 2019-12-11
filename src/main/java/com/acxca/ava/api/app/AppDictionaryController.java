package com.acxca.ava.api.app;

import com.acxca.ava.config.Properties;
import com.acxca.ava.consts.CustomMessageMap;
import com.acxca.ava.entity.*;
import com.acxca.ava.repository.DictionaryRepository;
import com.acxca.components.java.entity.BusinessException;
import com.acxca.components.java.util.DateUtil;
import com.acxca.components.spring.jwt.JwtUserDetail;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "app/dictionary",produces= MediaType.APPLICATION_JSON_VALUE)
public class AppDictionaryController {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private Properties properties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DateUtil dateUtil;


    @RequestMapping(path = "word/stat",method = RequestMethod.GET)
    public ResponseEntity<Object> getMyWords() {
        JwtUserDetail ud = (JwtUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 用户的所有词
        List<UserWord> words = dictionaryRepository.selectUserWords(ud.getId());
        // 需要复习的词
        List<UserWord> words_need_review = dictionaryRepository.selectNeedReviewUserWords(ud.getId());

        List<UserWordStat> ret = new ArrayList<>();
        int total_count,notstart_count,learning_count,finished_count,needreview_count;
        // 按语种分组
        List<Integer> langs = words.stream().map(w->w.getLang()).distinct().collect(Collectors.toList());
        for(int lang : langs){
            List<UserWord> w_lang = words.stream().filter(w->w.getLang() == lang).collect(Collectors.toList());
            total_count = w_lang.size();
            notstart_count = (int)w_lang.stream().filter(w->w.getLast_review_time() == null).count();
            learning_count = (int)w_lang.stream().filter(w->w.getLast_review_time() != null && !w.isFinished()).count();
            finished_count = (int)w_lang.stream().filter(w->w.isFinished()).count();

            needreview_count = (int)words_need_review.stream().filter(w->w.getLang() == lang).count();

            UserWordStat us = new UserWordStat(lang,total_count,notstart_count,learning_count,finished_count,needreview_count);

            // 最近一次学习
            LearnRecord lr = dictionaryRepository.selectLastLearRecord(ud.getId(),lang);
            if(lr != null){
                us.setLast_learn_time(lr.getStart_time());
                us.setLast_learn_count(lr.getWord_count());
            }

            ret.add(us);
        }

        return new ResponseEntity(ret, HttpStatus.OK);
    }

    @RequestMapping(path = "word/restart",method = RequestMethod.POST)
    public ResponseEntity<Object> restartWrods(@RequestBody String[] words) {

        //重置单词进度
        dictionaryRepository.resetUserWordsProgress(words);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "word/learn_new/{lang}/{count}",method = RequestMethod.GET)
    public ResponseEntity<Object> learNewWords(@PathVariable int lang,@PathVariable int count) {
        JwtUserDetail ud = (JwtUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //选取尚未学习的新词
        List<Word> words = dictionaryRepository.selectNewWords(ud.getId(),lang,count);

        //填充释义和例句
        fillInfo(words);

        return new ResponseEntity(words, HttpStatus.OK);
    }

    private void fillInfo(List<Word> words){
        if(words.size() == 0){
            return;
        }
        List<String> word_ids = words.stream().map((w)->w.getId()).collect(Collectors.toList());
        List<Explain> explains = dictionaryRepository.selectWordsExplains(word_ids);
        List<Sentence> sentences = dictionaryRepository.selectWordsSentences(word_ids);

        explains.forEach((expl)->{
            List<Sentence> ss = sentences.stream().filter((s)->s.getExplain_id().equals(expl.getId())).collect(Collectors.toList());
            expl.setSentences(ss);
        });

        words.forEach((w)->{
            w.setExplains(explains.stream().filter((s)->s.getWord_id().equals(w.getId())).collect(Collectors.toList()));
        });
    }

    @RequestMapping(path = "word/review_old/{lang}/{count}",method = RequestMethod.GET)
    public ResponseEntity<Object> reviewOldWords(@PathVariable int lang,@PathVariable int count) {
        JwtUserDetail ud = (JwtUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //选取需要复习的单词
        List<Word> words = dictionaryRepository.selectNeedReviewWords(ud.getId(),lang,count);

        //填充释义和例句
        fillInfo(words);

        return new ResponseEntity(words, HttpStatus.OK);
    }

    @RequestMapping(path = "learn/record/save",method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> saveLearningRecord(@RequestBody LearnRecord learnRecord) {
        JwtUserDetail ud = (JwtUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // learnRecord的id由前台生成
        learnRecord.setUser_id(ud.getId());
        // 合计次数
        learnRecord.setAnswer_times(learnRecord.getDetail().stream().mapToInt(l->l.getAnswer_times()).sum());
        learnRecord.setWrong_times(learnRecord.getDetail().stream().mapToInt(l->l.getWrong_times()).sum());

        //防止重复插入，如果重复，认为成功
        LearnRecord lr = dictionaryRepository.selectLearnRecord(learnRecord.getId());
        if(lr != null){
            return new ResponseEntity("", HttpStatus.OK);
        }

        learnRecord.getDetail().forEach(r->{
            r.setId(UUID.randomUUID().toString());
            r.setLearn_record_id(learnRecord.getId());
            //冗余信息方便查询
            r.setUser_id(learnRecord.getUser_id());
            r.setLearn_time(learnRecord.getEnd_time());

            //到最后一期，标记为学习结束
            r.setPhase(r.getPhase()+1);
            if(r.getPhase() == properties.getLearnPhaseMax()){
                //已经结束，不需要再复习，置空
                r.setNext_review_date(null);
                r.setFinished(true);
            }
            else {
                //如果未结束，计算出下次应该复习的日期
                Date today = dateUtil.getCurrentDate();
                int oneDay = 3600*24*1000;
                switch(r.getPhase()){
                    case 1:
                        r.setNext_review_date(new Date(today.getTime()+ properties.getLearnPhaseInterval1()*oneDay));
                        break;
                    case 2:
                        r.setNext_review_date(new Date(today.getTime()+ properties.getLearnPhaseInterval2()*oneDay));
                        break;
                    case 3:
                        r.setNext_review_date(new Date(today.getTime()+ properties.getLearnPhaseInterval3()*oneDay));
                        break;
                    case 4:
                        r.setNext_review_date(new Date(today.getTime()+ properties.getLearnPhaseInterval4()*oneDay));
                        break;
                }
            }

        });

        //插入学习记录
        dictionaryRepository.insertLearnRecord(learnRecord);
        dictionaryRepository.insertLearnRecordDetail(learnRecord.getDetail());

        //更新单词进度
        for(LearnRecordDetail ld: learnRecord.getDetail()){
            dictionaryRepository.updateUserWordProgress(ld);
        }

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "word/search",method = RequestMethod.GET)
    @CrossOrigin
    @Transactional
    public ResponseEntity<Object> searchWord(@RequestParam("lang") int lang,@RequestParam("form") String word_form){
        JwtUserDetail ud = (JwtUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String form = word_form.trim();

        // 先从本地查询
        Map ret = selectWords(lang, form);

        // 本地如果没有查询到，调用爬虫服务
        try {
            if(ret.isEmpty()) {
                HashMap<String, Object> param = new HashMap();
                param.put("lang", lang);
                param.put("form", form);
                String apiPath = properties.getSpiderServiceUrl() + properties.getSpiderServiceApiVocGrab();
                ResponseEntity<Word[]> resp = restTemplate.getForEntity(apiPath, Word[].class, param);

                Word[] ws = resp.getBody();
                for(Word w : ws) {
                    w.setId(UUID.randomUUID().toString());
                    dictionaryRepository.insertWord(w);
                    for (Explain exp : w.getExplains()) {
                        exp.setId(UUID.randomUUID().toString());
                        exp.setWord_id(w.getId());
                        dictionaryRepository.insertExplain(exp);
                        for (Sentence s : exp.getSentences()) {
                            s.setId(UUID.randomUUID().toString());
                            s.setExplain_id(exp.getId());
                            s.setWord_id(w.getId());
                            dictionaryRepository.insertSentence(s);
                        }
                    }

                    // 插入到用户词汇
                    insertUserWord(w,ud.getId());
                }

                if(ws.length >0) {
                    // 由于简体繁体的关系，静冈 查到的词是 静岡 ，spell和form都跟查询form不同，导致这里查不到
                    // 所以日语需要特殊处理
                    String newForm = form;
                    if(!Arrays.stream(ws).anyMatch(w->w.getSpell().equals(form) || w.getForms().contains(form))) {
                        newForm = ws[0].getSpell();
                    }

                    // 保存入库后再次查询
                    ret = selectWords(lang, newForm);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }

        //将查询结果返回给用户
        if(ret.isEmpty()) {
            throw new BusinessException(CustomMessageMap.WORD_NOT_FOUND);
        }

        return new ResponseEntity(ret, HttpStatus.OK);

    }

    private void insertUserWord(Word w,String user_id){
        // 检查是否已经存在
        UserWord ouw = dictionaryRepository.selectUserWord(user_id,w.getId());
        if(ouw != null){
            return;
        }

        UserWord uw = new UserWord();
        uw.setId(UUID.randomUUID().toString());
        uw.setUser_id(user_id);
        uw.setWord_id(w.getId());
        uw.setLang(w.getLang());
        uw.setPhase(0);
        uw.setFinished(false);
        uw.setLast_review_time(null);
        uw.setNext_review_date(null);

        dictionaryRepository.insertUserWord(uw);
    }

    private Map selectWords(int lang, String form) {
        Map ret = new HashMap();
        Word word = null;
        List<Word> words;
        if(lang == Lang.EN.getId()){
            words = dictionaryRepository.selectWordsByForm(lang,"["+form+"]");
        }
        else{
            words = dictionaryRepository.selectWordsByPronounceOrForm(lang,form,"["+form+"]");
        }

        if(words.size() != 0){
            //优先取spell一致的
            List<Word> sw = words.stream().filter(w->w.getSpell().equals(form)).collect(Collectors.toList());
            if(sw.size() > 0){
                word = sw.get(0);
            }
            else {
                word = words.get(0);
            }

            words.remove(word);
            List<String> similar = words.stream().map(w->w.getSpell()).collect(Collectors.toList());

            ret.put("word",word);
            ret.put("similar",similar);
        }

        return ret;
    }

    @RequestMapping(path = "word/list",method = RequestMethod.GET)
    public ResponseEntity<Object> getWordList(@RequestParam(value = "lang") Integer lang,
                                                 @RequestParam("pageSize") int pageSize,
                                                 @RequestParam("currentPage") int currentPage) {
        JwtUserDetail ud = (JwtUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

         List<Word> words = dictionaryRepository.selectWord6(ud.getId(),lang, pageSize * currentPage, pageSize);

        return new ResponseEntity(words, HttpStatus.OK);
    }
}
