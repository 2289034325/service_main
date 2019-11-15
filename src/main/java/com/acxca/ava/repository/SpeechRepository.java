package com.acxca.ava.repository;

import com.acxca.ava.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SpeechRepository {

    @Select({
            "<script>",
            "select a.*,b.name as media_name,b.path as media_path,b.time as media_time",
            "from article a left join media b on a.media_id = b.id where a.deleted=0",
            "<when test='lang != null'>",
            "and a.lang = #{lang} ",
            "</when>",
            "<when test='title != null and title != \"\"'>",
            "and a.title = #{title} ",
            "</when>",
            "</script>"
    })
    List<Article> selectArticles(@Param("lang") Integer lang, @Param("title") String title);

    @Select({
            "select a.*,b.name as media_name,b.type as media_type,b.path as media_path,b.time as media_time",
            "from article a left join media b on a.media_id = b.id where a.id=#{id} and a.deleted=0"
    })
    Article selectArticleById(@Param("id") int id);

    @Insert("insert into article (lang,title,description,performer,deleted) values(#{lang},#{title},#{description},#{performer},0)")
    @SelectKey(statement="select @@identity", keyProperty="id", before=false, resultType=Integer.class)
    Integer insertArticle(Article article);

    @Update("update article set lang=#{lang}, title = #{title},description=#{description},performer = #{performer} where id = #{id}")
    void updateArticle(Article article);

    //如果id发番到了5位数，一片文章有100个段落的话，该字段就达到600，这样的设计不够合理!!!
    //在段落表加上排序字段是可替代方案
//    @Update("update article set paragraph_sequence=#{paragraph_sequence} where id = #{id}")
//    void updateArticle2(@Param("id") int id, @Param("paragraph_sequence") String paragraph_sequence);

    @Update("update article set deleted = 1 where id = #{id}")
    void deleteArticle(@Param("id") int id);

    @Update("update article set media_id = #{media_id} where id = #{id}")
    void updateMediaId(@Param("id") int id, @Param("media_id") int media_id);

    @Insert("insert into media (name,type,path,time) values (#{name},#{type},#{path},#{time})")
    @SelectKey(statement="select @@identity", keyProperty="id", before=false, resultType=Integer.class)
    int insertMedia(Media media);

    @Select("select * from media where id=#{id}")
    Media selectMedia(int id);

    @Select("select * from paragraph where article_id=#{articleId} and deleted=0")
    List<Paragraph> selectAllParagraphsByArticleId(Integer articleId);

    @Select("select * from paragraph where id=#{id}")
    Paragraph selectParagraphById(int id);

    @Insert("insert into paragraph (article_id,text,translation,performer,deleted) values (#{article_id},#{text},#{translation},#{performer},0)")
    @SelectKey(statement="select @@identity", keyProperty="id", before=false, resultType=Integer.class)
    void insertParagraph(Paragraph paragraph);

    @Update("update paragraph set text=#{text},performer=#{performer} where id=#{id}")
    void updateParagraph(Paragraph paragraph);

    @Update("update paragraph set deleted=1 where id=#{id}")
    void deleteParagraph(int id);

    @Update("update split set deleted=1 where paragraph_id=#{paragraph_id}")
    void deleteSplits(int paragraph_id);

    @Insert("insert into split (article_id,paragraph_id,start_index,end_index,start_time,end_time,deleted) values (#{article_id},#{paragraph_id},#{start_index},#{end_index},#{start_time},#{end_time},0)")
    @SelectKey(statement="select @@identity", keyProperty="id", before=false, resultType=Integer.class)
    Integer insertSplit(ParagraphSplit split);

    @Insert({
            "<script>",
            "insert into split (article_id,paragraph_id,start_index,end_index,start_time,end_time,deleted)",
            "values ",
            "<foreach  collection='splits' item='split' separator=','>",
            "(#{split.article_id},#{split.paragraph_id},#{split.start_index},#{split.end_index},#{split.start_time},#{split.end_time},0)",
            "</foreach>",
            "</script>"
    })
    void insertSplits(@Param("splits") List<ParagraphSplit> splits);

    @Update("update split set start_time=#{start_time},end_time=#{end_time} where id=#{id}")
    void updateSplitTime(ParagraphSplit split);

    @Select("select * from split where paragraph_id=#{paragraphId}")
    List<ParagraphSplit> selectSplitsByParagraphId(Integer paragraphId);

    @Select("select * from split where id=#{id}")
    ParagraphSplit selectSplitById(int id);

    @Select("select * from split where article_id=#{articleId} and deleted=0")
    List<ParagraphSplit> selectSplitsByArticleId(Integer articleId);

    @Insert("INSERT INTO `ava`.`recite` (`article_id`, `paragraph_id`, `split_id`, `use_time`, `score`, `content`, `submit_time`) VALUES " +
            "(#{article_id}, #{paragraph_id}, #{split_id}, #{use_time}, #{score}, #{content}, #{submit_time});")
    @SelectKey(statement="select @@identity", keyProperty="id", before=false, resultType=Integer.class)
    // 如果加@Param，@SelectKey不生效，无法得到新发的id
    void insertSplitReciteRecord(ReciteRecord record);

    @Select("select * from recite where split_id=#{splitId}")
    List<ReciteRecord> selectSplitReciteHistory(int splitId);

    @Update("update recite set score=#{score} where id=#{id}")
    void updateReciteScore(@Param("id") int id, @Param("score") float score);
}
