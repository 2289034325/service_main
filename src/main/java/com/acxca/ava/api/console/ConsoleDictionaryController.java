package com.acxca.ava.api.console;

import com.acxca.ava.config.Properties;
import com.acxca.ava.consts.CustomMessageMap;
import com.acxca.ava.entity.Explain;
import com.acxca.ava.entity.Lang;
import com.acxca.ava.entity.Sentence;
import com.acxca.ava.entity.Word;
import com.acxca.ava.repository.DictionaryRepository;
import com.acxca.components.java.entity.BusinessException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "console/dictionary",produces= MediaType.APPLICATION_JSON_VALUE)
public class ConsoleDictionaryController {
    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private Properties properties;

    @Autowired
    private HttpServletRequest context;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(path = "word/single/{lang}/{form}",method = RequestMethod.GET)
    public ResponseEntity<Object> searchSingleWord(@PathVariable("lang") int lang,@PathVariable("form") String word_form) {

        String form = word_form.trim();

        Map ret = new HashMap();
        Word word = null;
        List<Word> words;
        if(lang == Lang.EN.getId()){
            words = dictionaryRepository.selectWordsByForm(lang,"["+form+"]");
        }
        else{
            words = dictionaryRepository.selectWordsByPronounceOrForm(lang,form,"["+form+"]");
        }

        if(!words.isEmpty())
        {
            //优先取spell一致的
            List<Word> sw = words.stream().filter(w->w.getSpell().equals(form)).collect(Collectors.toList());
            if(sw.size() > 0){
                word = sw.get(0);
            }
            else {
                word = words.get(0);
            }

            List<Explain> explains = dictionaryRepository.selectWordExplains(word.getId());
            List<Sentence> sentences = dictionaryRepository.selectWordSentences(word.getId());

            word.setExplains(explains);
            word.setSentences(sentences);

            words.remove(word);
            List<String> similar = words.stream().map(w->w.getSpell()).collect(Collectors.toList());

            ret.put("word",word);
            ret.put("similar",similar);
        }
        else{
            throw new BusinessException(CustomMessageMap.WORD_NOT_FOUND);
        }

        return new ResponseEntity(ret, HttpStatus.OK);
    }

    @RequestMapping(path = "word/list",method = RequestMethod.GET)
    public ResponseEntity<Object> searchWordList(@RequestParam(value = "lang", required=false) Integer lang,
                                                 @RequestParam(value = "spell", required=false) String spell,
                                                 @RequestParam("pageSize") int pageSize,
                                                 @RequestParam("currentPage") int currentPage) {
        if(spell!=null) {
            spell = spell.trim();
        }

        int count = 0;
        if(lang== null){
            count = dictionaryRepository.selectWord4Count(lang,spell);
        }
        else{
            if(lang.equals(Lang.JP.getId())){
                count = dictionaryRepository.selectWord5Count(lang,spell);
            }
            else{
                count = dictionaryRepository.selectWord4Count(lang,spell);
            }
        }

        List<Word> words = new ArrayList<>();
        if(count > 0) {
            if(lang== null){
                words = dictionaryRepository.selectWord4(lang, spell, pageSize * (currentPage-1), pageSize);
            }
            else{
                if(lang.equals(Lang.JP.getId())){
                    words = dictionaryRepository.selectWord5(lang, spell, pageSize * (currentPage-1), pageSize);
                }
                else{
                    words = dictionaryRepository.selectWord4(lang, spell, pageSize * (currentPage-1), pageSize);
                }
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("list",words);
        JSONObject pg = new JSONObject();
        pg.put("total",count);
        pg.put("pageSize",pageSize);
        pg.put("current",currentPage);
        obj.put("pagination",pg);

        return new ResponseEntity(obj, HttpStatus.OK);
    }

    @RequestMapping(path = "word",method = RequestMethod.POST)
    public ResponseEntity<Object> addWord(@RequestBody Word word) {

        //检查是否有同名的
        List<Word> words = dictionaryRepository.selectWordsBySpell(word.getLang(),word.getSpell());

        if(words.size() != 0){
            throw new BusinessException(CustomMessageMap.WORD_EXIST);
        }

        //词形至少包含原形
        if(word.getForms() == null || word.getForms().isEmpty()){
            word.setForms("["+word.getSpell()+"]");
        }
        else{
            //如果原形没有用[]包括，加上[]
            List<String> forms = Arrays.asList(word.getForms().split("[,，]",-1));
            forms = forms.stream().map(f->{
                if(!f.startsWith("[")){
                    f = "["+f;
                }
                if(!f.endsWith("]")){
                    f = f+"]";
                }
                return f;
            }).collect(Collectors.toList());
            String forms_str = String.join(",",forms);
            word.setForms(forms_str);
        }

        //插入
        word.setId(UUID.randomUUID().toString());
        dictionaryRepository.insertWord(word);

        return new ResponseEntity(word, HttpStatus.OK);
    }

    @RequestMapping(path = "word",method = RequestMethod.PUT)
    public ResponseEntity<Object> modifyWord(@RequestBody Word word) {

        //检查是否有同名的
        List<Word> words = dictionaryRepository.selectWordsByIdAndSpell(word.getId(), word.getLang(),word.getSpell());
        if(words.size() != 0){
            throw new BusinessException(CustomMessageMap.WORD_EXIST);
        }

        //更新
        dictionaryRepository.updateWord(word);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "word",method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteWord(@RequestBody Word word){

        dictionaryRepository.softDeletedWord(word.getId());

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "explain",method = RequestMethod.POST)
    public ResponseEntity<Object> addExplain(@RequestBody Explain explain) {

        explain.setId(UUID.randomUUID().toString());
        dictionaryRepository.insertExplain(explain);

        return new ResponseEntity(explain, HttpStatus.OK);
    }

    @RequestMapping(path = "explain",method = RequestMethod.PUT)
    public ResponseEntity<Object> modifyExplain(@RequestBody Explain explain) {

        dictionaryRepository.updateExplain(explain);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "explain",method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteExplain(@RequestBody Explain explain) throws Exception{

        dictionaryRepository.softDeletedExplain(explain.getId());

        return new ResponseEntity("", HttpStatus.OK);
    }


    @RequestMapping(path = "sentence",method = RequestMethod.POST)
    public ResponseEntity<Object> addSentence(@RequestBody Sentence sentence) {

        sentence.setId(UUID.randomUUID().toString());
        dictionaryRepository.insertSentence(sentence);

        return new ResponseEntity(sentence, HttpStatus.OK);
    }

    @RequestMapping(path = "sentence",method = RequestMethod.PUT)
    public ResponseEntity<Object> modifySentence(@RequestBody Sentence sentence) {

        dictionaryRepository.updateSentence(sentence);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "sentence",method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteSentence(@RequestBody Sentence sentence){

        dictionaryRepository.softDeletedSentence(sentence.getId());

        return new ResponseEntity("", HttpStatus.OK);
    }
}
