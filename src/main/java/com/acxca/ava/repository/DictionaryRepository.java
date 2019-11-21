package com.acxca.ava.repository;

import com.acxca.ava.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface DictionaryRepository {

    @Select("select * from `vocab.user_word` where user_id=#{user_id} and deleted=0")
    List<UserWord> selectUserWords(String user_id);

    @Update({
            "<script>",
            "update `vocab.user_word`",
            "set phase=0,finished=false,last_review_time=null,next_review_date=null",
            "where book_id=#{book_id} ",
            "and word_id in ",
            "<foreach item='word_id' collection='words' open='(' separator=',' close=')'>",
            "#{word_id}",
            "</foreach>",
            "</script>"
    })
    void resetUserWordsProgress(@Param("words") String[] words);

    @Select({"select b.*",
            "from `vocab.user_word` a",
            "inner join `vocab.word` b",
            "on a.word_id = b.id",
            "where a.user_id = #{user_id}",
            "and a.lang = #{lang}",
            "and a.deleted = 0",
            "and a.last_review_time is null",
            "limit #{count}"
    })
    List<Word> selectNewWords(@Param("user_id") String user_id, @Param("lang") int lang,@Param("count") int count);

    @Select({"select b.*,a.phase as learn_phase",
            "from `vocab.user_word` a",
            "inner join `vocab.word` b",
            "on a.word_id = b.id",
            "where a.finished = 0",
            "and a.next_review_date <= CURRENT_DATE",
            "and DATE(a.last_review_time) < CURRENT_DATE",
            "and a.user_id = #{user_id}",
            "and a.lang = #{lang}",
            "and a.deleted = 0",
            "limit #{count}"
    })
    List<Word> selectNeedReviewWords(@Param("user_id") String user_id, @Param("lang") int lang,@Param("count") int count);

    @Select({"select *",
            "from `vocab.user_word`",
            "where finished = 0",
            "and next_review_date <= CURRENT_DATE",
            "and DATE(last_review_time) < CURRENT_DATE",
            "and user_id = #{user_id}",
            "and deleted = 0"
    })
    List<UserWord> selectNeedReviewUserWords(@Param("user_id") String user_id);

    @Select({"select * from `vocab.learn_record` where user_id=#{user_id} and lang=#{lang} order by start_time desc limit 1"})
    LearnRecord selectLastLearRecord(@Param("user_id") String user_id,@Param("lang") int lang);

    @Select({
            "<script>",
            "select * from `vocab.explain`",
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
            "select * from `vocab.sentence`",
            "where deleted=0",
            "and word_id in",
            "<foreach item='word_id' collection='word_ids' open='(' separator=',' close=')'>",
            "#{word_id}",
            "</foreach>",
            "</script>"
    })
    List<Sentence> selectWordsSentences(@Param("word_ids") List<String> word_ids);

    @Select("select * from `vocab.learn_record` where id=#{id}")
    LearnRecord selectLearnRecord(@Param("id") String id);

    @Insert({"INSERT INTO `vocab.learn_record` ",
            "(`id`, `user_id`, `lang`, `word_count`, `answer_times`, `wrong_times`, `start_time`, `end_time`, `deleted`) VALUES ",
            "(#{id}, #{user_id}, #{lang}, #{word_count}, #{answer_times}, #{wrong_times}, #{start_time}, #{end_time},0)"})
    void insertLearnRecord(LearnRecord lr);

    @Insert({
            "<script>",
            "INSERT INTO `vocab.learn_record_detail` (`id`,`learn_record_id`, `user_id`, `lang`, `word_id`, `answer_times`, `wrong_times`, `learn_time`, `deleted`)",
            "values ",
            "<foreach  collection='details' item='detail' separator=','>",
            "(#{detail.id},#{detail.learn_record_id}, #{detail.user_id}, #{detail.lang}, #{detail.word_id}, #{detail.answer_times}, #{detail.wrong_times}, #{detail.learn_time}, 0)",
            "</foreach>",
            "</script>"
    })
    void insertLearnRecordDetail(@Param("details") List<LearnRecordDetail> details);

//    @Update({
//            "<script>",
//            "<foreach  collection='details' item='detail' separator=';'>",
//            "UPDATE `vocab.user_word` SET phase=#{detail.phase}, last_review_time=NOW(), next_review_date=#{detail.next_review_date}, finished=#{detail.finished}",
//            "WHERE user_id=#{detail.user_id} AND word_id=#{detail.word_id}",
//            "</foreach>",
//            "</script>"})
    @Update({"UPDATE `vocab.user_word`",
            "SET phase=#{phase}, last_review_time=NOW(), next_review_date=#{next_review_date},",
            "finished=#{finished}, answer_times=answer_times+#{answer_times}, wrong_times=wrong_times+#{wrong_times}",
            "WHERE user_id=#{user_id} AND word_id=#{word_id}"})
    void updateUserWordProgress(LearnRecordDetail details);


    @Select("select * from `vocab.word` where lang=#{lang} and forms like '%' #{form} '%' and deleted=0")
    List<Word> selectWordsByForm(@Param("lang") int lang,@Param("form") String form);

    @Select("select * from `vocab.word` where lang=#{lang} and (pronounce = #{pronounce} or forms like '%' #{form} '%') and deleted=0")
    List<Word> selectWordsByPronounceOrForm(@Param("lang") int lang,@Param("pronounce") String pronounce,@Param("form") String form);

    @Select("select * from `vocab.explain` where word_id=#{word_id} and deleted=0")
    List<Explain> selectWordExplains(@Param("word_id") String word_id);

    @Select("select * from `vocab.sentence` where word_id=#{word_id} and deleted=0")
    List<Sentence> selectWordSentences(@Param("word_id") String word_id);

    @Select({"<script>",
            "select count(*) from `vocab.word` w",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and w.spell = #{spell} ",
            "</when>",
            "</script>"})
    int selectWord4Count(@Param("lang") Integer lang, @Param("spell") String spell);

    @Select({"<script>",
            "select w.* from `vocab.word` w",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and w.spell = #{spell} ",
            "</when>",
            "order by id desc",
            "limit #{skip},#{count}",
            "</script>"})
    List<Word> selectWord4(@Param("lang") Integer lang, @Param("spell") String spell, @Param("skip") Integer skip, @Param("count") Integer count);

    @Select({"<script>",
            "select count(*) from `vocab.word` w",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and (w.spell = #{spell} or w.pronounce = #{spell})",
            "</when>",
            "</script>"})
    int selectWord5Count(@Param("lang") Integer lang, @Param("spell") String spell);

    @Select({"<script>",
            "select w.* from `vocab.word` w",
            "where w.deleted=0",
            "<when test='lang != null'>",
            "and w.lang = #{lang} ",
            "</when>",
            "<when test='spell != null and spell != \"\"'>",
            "and (w.spell = #{spell} or w.pronounce = #{spell})",
            "</when>",
            "order by id desc",
            "limit #{skip},#{count}",
            "</script>"})
    List<Word> selectWord5(@Param("lang") Integer lang, @Param("spell") String spell, @Param("skip") Integer skip, @Param("count") Integer count);

    @Select("select * from `vocab.word` where lang=#{lang} and spell=#{spell} and deleted=0")
    List<Word> selectWordsBySpell(@Param("lang") Integer lang, @Param("spell") String spell);

    @Select("select * from `vocab.word` where id=#{id}")
    Word selectWordById(@Param("id") String id);

    @Insert("insert into `vocab.word` (id,lang,spell,pronounce,meaning,forms,deleted) values(#{id},#{lang},#{spell},#{pronounce},#{meaning},#{forms},0)")
    void insertWord(Word word);

    @Select("select * from `vocab.word` where lang=#{lang} and spell=#{spell} and deleted=0 and id <> #{id}")
    List<Word> selectWordsByIdAndSpell(@Param("id") String id,@Param("lang") Integer lang, @Param("spell") String spell);

    @Update("update `vocab.word` set lang = #{lang},spell = #{spell},pronounce = #{pronounce},forms=#{forms},meaning=#{meaning} where id = #{id}")
    void updateWord(Word word);

    @Update("update `vocab.word` set deleted=1 where id = #{id}")
    void softDeletedWord(@Param("id") String id);

    @Insert("insert into `vocab.explain` (id,word_id,pronounce,`explain`,deleted) values(#{id},#{word_id},#{pronounce},#{explain},0)")
    void insertExplain(Explain explain);

    @Update("update `vocab.explain` set pronounce = #{pronounce}, `explain` = #{explain} where id = #{id}")
    void updateExplain(Explain explain);

    @Update("update `vocab.explain` set deleted=1 where id = #{id}")
    void softDeletedExplain(@Param("id") String id);

    @Insert("insert into `vocab.sentence` (id,word_id,explain_id,word,sentence,translation,deleted) values(#{id},#{word_id},#{explain_id},#{word},#{sentence},#{translation},0)")
    void insertSentence(Sentence sentence);

    @Update("update `vocab.sentence` set word = #{word},sentence = #{sentence},translation = #{translation} where id = #{id}")
    void updateSentence(Sentence sentence);

    @Update("update `vocab.sentence` set deleted=1 where id = #{id}")
    void softDeletedSentence(@Param("id") String id);

    @Insert({"INSERT INTO `vocab.user_word`",
            "(`id`, `user_id`, `word_id`, `lang`, `phase`, `finished`,`answer_times`,`wrong_times`, `last_review_time`, `next_review_date`, `add_time`, `deleted`)",
            "VALUES",
            "(#{id}, #{user_id}, #{word_id}, #{lang}, #{phase}, #{finished},#{answer_times},#{wrong_times}, #{last_review_time}, #{next_review_date}, NOW(), 0)"})
    void insertUserWord(UserWord uw);

    @Select({"select * from `vocab.user_word` where user_id=#{user_id} and word_id=#{word_id} and deleted=0"})
    UserWord selectUserWord(@Param("user_id") String user_id,@Param("word_id") String word_id);
}
