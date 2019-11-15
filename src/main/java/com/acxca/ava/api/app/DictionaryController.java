package com.acxca.ava.api.app;

import com.acxca.ava.config.Properties;
import com.acxca.ava.consts.CustomMessageMap;
import com.acxca.ava.entity.*;
import com.acxca.ava.repository.DictionaryRepository;
import com.acxca.components.java.entity.BusinessException;
import com.acxca.components.java.util.DateUtil;
import com.acxca.components.spring.jwt.JwtCertificate;
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
@RequestMapping(path = "dictionary",produces= MediaType.APPLICATION_JSON_VALUE)
public class DictionaryController {

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
        JwtCertificate ud = (JwtCertificate)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 取改用户的所有词
        List<UserWord> words = dictionaryRepository.selectUserWords(ud.getUserId());

        List<UserWordStat> ret = new ArrayList<>();
        int total_count,notstart_count,learning_count,finished_count;
        // 按语种分组
        List<Integer> langs = words.stream().map(w->w.getLang()).distinct().collect(Collectors.toList());
        for(int lang : langs){
            List<UserWord> w_lang = words.stream().filter(w->w.getLang() == lang).collect(Collectors.toList());
            total_count = w_lang.size();
            notstart_count = (int)w_lang.stream().filter(w->w.getLast_review_time() == null).count();
            learning_count = (int)w_lang.stream().filter(w->w.getLast_review_time() != null && !w.isFinished()).count();
            finished_count = (int)w_lang.stream().filter(w->w.isFinished()).count();

            ret.add(new UserWordStat(lang,total_count,notstart_count,learning_count,finished_count));
        }

        return new ResponseEntity(ret, HttpStatus.OK);
    }

    @RequestMapping(path = "word/restart",method = RequestMethod.POST)
    public ResponseEntity<Object> restartWrods(@RequestBody String[] words) {

        //重置单词进度
        dictionaryRepository.updateUserWordsProgress(words);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "word/learn_new/{lang}/{count}",method = RequestMethod.GET)
    public ResponseEntity<Object> learNewWords(@PathVariable int lang,@PathVariable int count) {
        JwtCertificate ud = (JwtCertificate)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //选取尚未学习的新词
        List<Word> words = dictionaryRepository.selectNewWords(ud.getUserId(),lang,count);

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
        JwtCertificate ud = (JwtCertificate)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //选取需要复习的单词
        List<Word> words = dictionaryRepository.selectNeedReviewWords(ud.getUserId(),lang,count);

        //填充释义和例句
        fillInfo(words);

        return new ResponseEntity(words, HttpStatus.OK);
    }

    @RequestMapping(path = "learn/record/save",method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> saveLearningRecord(@RequestBody LearnRecord learnRecord) {
        JwtCertificate ud = (JwtCertificate)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        learnRecord.setUser_id(ud.getId());

        //防止重复插入，如果重复，认为成功
        LearnRecord lr = dictionaryRepository.selectLearnRecord(learnRecord.getId());
        if(lr != null){
            return new ResponseEntity("", HttpStatus.OK);
        }

        learnRecord.getDetail().forEach(r->{
            r.setLearn_record_id(learnRecord.getId());
            //冗余信息方便查询
            r.setUser_id(learnRecord.getUser_id());

            r.setLearn_time(learnRecord.getEnd_time());
            //到最后一期，标记为学习结束
            r.setLearn_phase(r.getLearn_phase()+1);
            if(r.getLearn_phase() == properties.getLearnPhaseMax()){
                //已经结束，不需要再复习。随便给个日期
                r.setNext_learn_date(new Date());
                r.setFinished(true);
            }
            else {
                //如果未结束，计算出下次应该复习的日期
                Date today = dateUtil.getCurrentDate();
                switch(r.getLearn_phase()){
                    case 1:
                        r.setNext_learn_date(new Date(today.getTime()+ properties.getLearnPhaseInterval1()*3600*24*1000));
                        break;
                    case 2:
                        r.setNext_learn_date(new Date(today.getTime()+ properties.getLearnPhaseInterval2()*3600*24*1000));
                        break;
                    case 3:
                        r.setNext_learn_date(new Date(today.getTime()+ properties.getLearnPhaseInterval3()*3600*24*1000));
                        break;
                    case 4:
                        r.setNext_learn_date(new Date(today.getTime()+ properties.getLearnPhaseInterval4()*3600*24*1000));
                        break;
                }
            }

        });

        //插入学习记录
        dictionaryRepository.insertLearnRecord(learnRecord);
        dictionaryRepository.insertLearnRecordWord(learnRecord.getDetail());
        //插入单词进度
        dictionaryRepository.updateUserWordProgress(learnRecord.getDetail());

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "word/search",method = RequestMethod.GET)
    @CrossOrigin
    public ResponseEntity<Object> searchWord(@RequestParam("lang") int lang,@RequestParam("form") String word_form){
        JwtCertificate ud = (JwtCertificate)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
                restTemplate.getForEntity(apiPath, String.class, param);

                // 爬虫服务爬到数据后会保存入库，这里再次查询
                // TODO 爬虫服务不应该直接保存入库，应当在这里保存
                ret = selectWords(lang, form);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将查询结果返回给用户
        if(ret.isEmpty()) {
            throw new BusinessException(CustomMessageMap.WORD_NOT_FOUND);
        }

        return new ResponseEntity(ret, HttpStatus.OK);

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
}
