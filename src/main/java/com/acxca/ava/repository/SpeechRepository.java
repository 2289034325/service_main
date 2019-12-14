package com.acxca.ava.repository;

import com.acxca.ava.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SpeechRepository {

    @Select({
            "<script>",
            "select *",
            "from `speech.article`",
            "where deleted=0",
            "<when test='lang != null'>",
            "and lang = #{lang} ",
            "</when>",
            "<when test='title != null and title != \"\"'>",
            "and title like '%' #{title} '%' ",
            "</when>",
            "</script>"
    })
    List<Article> selectArticles(@Param("lang") Integer lang, @Param("title") String title);

    @Select({
            "select *",
            "from `speech.article`",
            "where id=#{id} and deleted=0"
    })
    Article selectArticleById(@Param("id") String id);

    @Insert("insert into `speech.article` (id,lang,title,description,performer,deleted) values(#{id},#{lang},#{title},#{description},#{performer},0)")
    Integer insertArticle(Article article);

    @Update("update `speech.article` set lang=#{lang}, title = #{title},description=#{description},performer = #{performer} where id = #{id}")
    void updateArticle(Article article);

    //如果id发番到了5位数，一片文章有100个段落的话，该字段就达到600，这样的设计不够合理!!!
    //在段落表加上排序字段是可替代方案
//    @Update("update article set paragraph_sequence=#{paragraph_sequence} where id = #{id}")
//    void updateArticle2(@Param("id") int id, @Param("paragraph_sequence") String paragraph_sequence);

    @Update("update `speech.article` set deleted = 1 where id = #{id}")
    void deleteArticle(@Param("id") String id);

    @Insert("insert into `speech.media` (id,name,path,time) values (#{id},#{name},#{path},#{time})")
    void insertMedia(Media media);

    @Insert("insert into `speech.article_media` (id,article_id,media_id,media_usage,deleted) values (#{id},#{article_id},#{media_id},#{media_usage},0)")
    void insertArticleMediaR(@Param("id") String id,
                             @Param("article_id") String article_id,
                             @Param("media_id") String media_id,
                             @Param("media_usage") int media_usage);

    @Select("select * from `speech.media` where id=#{id}")
    Media selectMedia(String id);

    @Select("select * from `speech.paragraph` where article_id=#{articleId} and deleted=0")
    List<Paragraph> selectAllParagraphsByArticleId(String articleId);

    @Select("select * from `speech.paragraph` where id=#{id}")
    Paragraph selectParagraphById(String id);

    @Insert("insert into `speech.paragraph` (id,article_id,`index`,text,translation,performer,deleted) values (#{id},#{article_id},#{index},#{text},#{translation},#{performer},0)")
    void insertParagraph(Paragraph paragraph);

    @Update("update `speech.paragraph` set text=#{text},performer=#{performer} where id=#{id}")
    void updateParagraph(Paragraph paragraph);

    @Update("update `speech.paragraph` set deleted=1 where id=#{id}")
    void deleteParagraph(String id);

    @Update("update `speech.split` set deleted=1 where paragraph_id=#{paragraph_id}")
    void deleteSplits(String paragraph_id);

    @Insert("insert into `speech.split` (id,article_id,paragraph_id,`index`,start_index,end_index,start_time,end_time,deleted) values (#{id},#{article_id},#{paragraph_id},#{index},#{start_index},#{end_index},#{start_time},#{end_time},0)")
    void insertSplit(ParagraphSplit split);

    @Insert({
            "<script>",
            "insert into `speech.split` (id,article_id,paragraph_id,`index`,start_index,end_index,start_time,end_time,deleted)",
            "values ",
            "<foreach  collection='splits' item='split' separator=','>",
            "(#{split.id},#{split.article_id},#{split.paragraph_id},#{split.index},#{split.start_index},#{split.end_index},#{split.start_time},#{split.end_time},0)",
            "</foreach>",
            "</script>"
    })
    void insertSplits(@Param("splits") List<ParagraphSplit> splits);

    @Update("update `speech.split` set start_time=#{start_time},end_time=#{end_time} where id=#{id}")
    void updateSplitTime(ParagraphSplit split);

    @Select("select * from `speech.split` where paragraph_id=#{paragraphId} and deleted=0")
    List<ParagraphSplit> selectSplitsByParagraphId(String paragraphId);

    @Select("select * from `speech.split` where id=#{id}")
    ParagraphSplit selectSplitById(String id);

    @Select("select * from `speech.split` where article_id=#{articleId} and deleted=0")
    List<ParagraphSplit> selectSplitsByArticleId(String articleId);

    @Select({
            "select c.*,b.media_usage as `usage`",
            "from `speech.article` a",
            "inner join `speech.article_media` b on a.id=b.article_id and b.deleted=0",
            "inner join `speech.media` c on b.media_id=c.id",
            "where a.id=#{id}"
    })
    List<Media> selectMediasByArticleId(@Param("id") String id);

    @Insert("INSERT INTO `speech.recite` (`id`,`user_id`, `article_id`, `paragraph_id`, `split_id`, `use_time`, `score`, `content`, `submit_time`) VALUES " +
            "(#{id},#{user_id},#{article_id}, #{paragraph_id}, #{split_id}, #{use_time}, #{score}, #{content}, #{submit_time});")
    void insertSplitReciteRecord(ReciteRecord record);

    @Select("select * from `speech.recite` where user_id=#{user_id} and split_id=#{split_id}")
    List<ReciteRecord> selectSplitReciteHistory(@Param("user_id") String user_id, @Param("split_id") String split_id);

    @Update("update `speech.recite` set score=#{score} where id=#{id}")
    void updateReciteScore(@Param("id") String id, @Param("score") float score);
}
