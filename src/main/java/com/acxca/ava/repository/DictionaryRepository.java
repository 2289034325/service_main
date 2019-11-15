package com.acxca.ava.repository;

import com.acxca.ava.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface DictionaryRepository {

    @Select("select * from user_word where user_id=#{user_id} and deleted=0")
    List<UserWord> selectUserWords(String user_id);

    @Update({
            "<script>",
            "update user_word",
            "set phase=0,finished=false,last_review_time=null,next_review_date=null",
            "where book_id=#{book_id} ",
            "and word_id in ",
            "<foreach item='word_id' collection='words' open='(' separator=',' close=')'>",
            "#{word_id}",
            "</foreach>",
            "</script>"
    })
    void updateUserWordsProgress(@Param("words") String[] words);

    @Select({"select c.*",
            "from user_word a",
            "inner join word b",
            "on a.word_id = b.id",
            "where a.user_id = #{user_id}",
            "and a.lang = #{lang}",
            "and a.deleted = 0",
            "limit #{count}"
    })
    List<Word> selectNewWords(@Param("user_id") String user_id, @Param("lang") int lang,@Param("count") int count);

    @Select({"select b.*,a.phase as learn_phase",
            "from user_word a",
            "inner join word b",
            "on a.word_id = b.id",
            "where a.finished = 0",
            "and a.next_review_date <= CURRENT_DATE",
            "and DATE(a.update_time) < CURRENT_DATE",
            "and a.user_id = #{user_id}",
            "and a.lang = #{lang}",
            "and a.deleted = 0",
            "limit #{count}"
    })
    List<Word> selectNeedReviewWords(@Param("user_id") String user_id, @Param("lang") int lang,@Param("count") int count);

    @Select({
            "<script>",
            "select * from `explain`",
            "where deleted=0",
            "and word_id in",
            "<foreach item='word_id' collection='word_ids' open='(' separator=',' close=')'>",
            "#{word_id}",
            "</foreach>",
            "</script>"
    })
    List<Explain> selectWordsExplains(@Param("word_ids") List<String> word_ids);

    @Select({
            "<script>",
            "select * from sentence",
            "where deleted=0",
            "and word_id in",
            "<foreach item='word_id' collection='word_ids' open='(' separator=',' close=')'>",
            "#{word_id}",
            "</foreach>",
            "</script>"
    })
    List<Sentence> selectWordsSentences(@Param("word_ids") List<String> word_ids);

    @Select("select * from user_learn_record where id=#{id}")
    LearnRecord selectLearnRecord(@Param("id") String id);

    @Insert({"INSERT INTO `user_learn_record` ",
            "(`id`, `user_id`, `book_id`, `user_book_id`, `word_count`, `answer_times`, `wrong_times`, `start_time`, `end_time`, `deleted`) VALUES ",
            "(#{id}, #{user_id}, #{book_id}, #{user_book_id}, #{word_count}, #{answer_times}, #{wrong_times}, #{start_time}, #{end_time},0)"})
    void insertLearnRecord(LearnRecord lr);

    @Insert({
            "<script>",
            "INSERT INTO `user_learn_record_word` (`learn_record_id`, `user_id`, `book_id`, `user_book_id`, `word_id`, `answer_times`, `wrong_times`, `learn_time`, `deleted`)",
            "values ",
            "<foreach  collection='words' item='word' separator=','>",
            "(#{word.learn_record_id}, #{word.user_id}, #{word.book_id}, #{word.user_book_id}, #{word.word_id}, #{word.answer_times}, #{word.wrong_times}, #{word.learn_time}, 0)",
            "</foreach>",
            "</script>"
    })
    void insertLearnRecordWord(@Param("words") List<LearnRecordWord> lrw);

    @Update({
            "<script>",
            "INSERT INTO user_word_progress ",
            "(`user_id`, `user_book_id`, `word_id`, `phase`, `next_review_date`, `finished`, `update_time`)",
            "VALUES",
            "<foreach  collection='words' item='word' separator=','>",
            "(#{word.user_id}, #{word.user_book_id}, #{word.word_id}, #{word.learn_phase}, #{word.next_learn_date}, #{word.finished}, NOW())",
            "</foreach>",
            "ON DUPLICATE KEY UPDATE",
            "phase=values(phase), next_review_date=values(next_review_date),finished=values(finished),update_time=values(update_time)",
            "</script>"})
    void updateUserWordProgress(@Param("words") List<LearnRecordWord> lrw);

    @Select("select * from word where lang=#{lang} and forms like '%' #{form} '%' and deleted=0")
    List<Word> selectWordsByForm(@Param("lang") int lang,@Param("form") String form);

    @Select("select * from word where lang=#{lang} and (pronounce = #{pronounce} or forms like '%' #{form} '%') and deleted=0")
    List<Word> selectWordsByPronounceOrForm(@Param("lang") int lang,@Param("pronounce") String pronounce,@Param("form") String form);

    @Select("select * from `explain` where word_id=#{word_id} and deleted=0")
    List<Explain> selectWordExplains(@Param("word_id") String word_id);

    @Select("select * from sentence where word_id=#{word_id} and deleted=0")
    List<Sentence> selectWordSentences(@Param("word_id") String word_id);

    @Select({"<script>",
            "select count(*) from word w",
            "<when test='book_id != null'>",
            "inner join book_word bw",
            "on w.id = bw.word_id",
            "and bw.deleted=0",
            "</when>",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and w.spell = #{spell} ",
            "</when>",
            "<when test='book_id != null'>",
            "and bw.book_id = #{book_id} ",
            "</when>",
            "</script>"})
    int selectWord4Count(@Param("lang") int lang, @Param("spell") String spell, @Param("book_id") String book_id);

    @Select({"<script>",
            "select count(*) from word w",
            "<when test='book_id != null'>",
            "inner join book_word bw",
            "on w.id = bw.word_id",
            "and bw.deleted=0",
            "</when>",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and (w.spell = #{spell} or w.pronounce = #{spell})",
            "</when>",
            "<when test='book_id != null'>",
            "and bw.book_id = #{book_id} ",
            "</when>",
            "</script>"})
    int selectWord5Count(@Param("lang") int lang, @Param("spell") String spell, @Param("book_id") String book_id);

    @Select({"<script>",
            "select w.* from word w",
            "<when test='book_id != null'>",
            "inner join book_word bw",
            "on w.id = bw.word_id",
            "and bw.deleted=0",
            "</when>",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and w.spell = #{spell} ",
            "</when>",
            "<when test='book_id != null'>",
            "and bw.book_id = #{book_id} ",
            "</when>",
            "order by id desc",
            "limit #{skip},#{count}",
            "</script>"})
    List<Word> selectWord4(@Param("lang") Integer lang, @Param("spell") String spell, @Param("book_id") String book_id, @Param("skip") Integer skip, @Param("count") Integer count);

    @Select({"<script>",
            "select w.* from word w",
            "<when test='book_id != null'>",
            "inner join book_word bw",
            "on w.id = bw.word_id",
            "and bw.deleted=0",
            "</when>",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and (w.spell = #{spell} or w.pronounce = #{spell})",
            "</when>",
            "<when test='book_id != null'>",
            "and bw.book_id = #{book_id} ",
            "</when>",
            "order by id desc",
            "limit #{skip},#{count}",
            "</script>"})
    List<Word> selectWord5(@Param("lang") Integer lang, @Param("spell") String spell, @Param("book_id") String book_id, @Param("skip") Integer skip, @Param("count") Integer count);

    @Select("select * from word where lang=#{lang} and spell=#{spell} and deleted=0")
    List<Word> selectWordsBySpell(@Param("lang") Integer lang, @Param("spell") String spell);

    @Insert("insert into word (lang,spell,pronounce,meaning,forms,deleted) values(#{lang},#{spell},#{pronounce},#{meaning},#{forms},0)")
    void insertWord(Word word);

    @Select("select * from word where lang=#{lang} and spell=#{spell} and deleted=0 and id <> #{id}")
    List<Word> selectWordsByIdAndSpell(@Param("id") String id,@Param("lang") Integer lang, @Param("spell") String spell);

    @Update("update word set lang = #{lang},spell = #{spell},pronounce = #{pronounce},forms=#{forms},meaning=#{meaning} where id = #{id}")
    void updateWord(Word word);

    @Update("update word set deleted=1 where id = #{id}")
    void softDeletedWord(@Param("id") String id);

    @Insert("insert into `explain` (word_id,pronounce,`explain`,deleted) values(#{word_id},#{pronounce},#{explain},0)")
    void insertExplain(Explain explain);

    @Update("update `explain` set pronounce = #{pronounce}, `explain` = #{explain} where id = #{id}")
    void updateExplain(Explain explain);

    @Update("update `explain` set deleted=1 where id = #{id}")
    void softDeletedExplain(@Param("id") Integer id);

    @Insert("insert into sentence (word_id,explain_id,word,sentence,translation,deleted) values(#{word_id},#{explain_id},#{word},#{sentence},#{translation},0)")
    void insertSentence(Sentence sentence);

    @Update("update sentence set word = #{word},sentence = #{sentence},translation = #{translation} where id = #{id}")
    void updateSentence(Sentence sentence);

    @Update("update sentence set deleted=1 where id = #{id}")
    void softDeletedSentence(@Param("id") Integer id);
}
