package com.acxca.ava.repository;

import com.acxca.ava.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface WritingRepository {

    @Select({
            "<script>",
            "select *",
            "from `writing.article`",
            "where deleted=0",
            "<when test='lang != null'>",
            "and lang = #{lang} ",
            "</when>",
            "<when test='title != null and title != \"\"'>",
            "and title like '%' #{title} '%' ",
            "</when>",
            "</script>"
    })
    List<WritingArticle> selectArticles(@Param("lang") Integer lang, @Param("title") String title);

    @Select({
            "select *",
            "from `writing.article`",
            "where id=#{id} and deleted=0"
    })
    WritingArticle selectArticleById(@Param("id") String id);

    @Insert("insert into `writing.article` (id,lang,source,title,description,insert_time,deleted) values(#{id},#{lang},#{source},#{title},#{description},#{insert_time},0)")
    Integer insertArticle(WritingArticle article);

    @Update("update `writing.article` set lang=#{lang}, source = #{source},title = #{title},description=#{description} where id = #{id}")
    void updateArticle(WritingArticle article);

    //如果id发番到了5位数，一片文章有100个段落的话，该字段就达到600，这样的设计不够合理!!!
    //在段落表加上排序字段是可替代方案
//    @Update("update article set paragraph_sequence=#{paragraph_sequence} where id = #{id}")
//    void updateArticle2(@Param("id") int id, @Param("paragraph_sequence") String paragraph_sequence);

    @Update("update `writing.article` set deleted = 1 where id = #{id}")
    void deleteArticle(@Param("id") String id);

    @Select("select * from `writing.paragraph` where article_id=#{articleId} and deleted=0")
    List<Paragraph> selectAllParagraphsByArticleId(String articleId);

    @Select("select * from `writing.paragraph` where id=#{id}")
    Paragraph selectParagraphById(String id);

    @Insert("insert into `writing.paragraph` (id,article_id,`index`,text,deleted) values (#{id},#{article_id},#{index},#{text},0)")
    void insertParagraph(Paragraph paragraph);

    @Update("update `writing.paragraph` set text=#{text} where id=#{id}")
    void updateParagraph(Paragraph paragraph);

    @Update("update `writing.paragraph` set deleted=1 where id=#{id}")
    void deleteParagraph(String id);

    @Update("update `writing.split` set deleted=1 where paragraph_id=#{paragraph_id}")
    void deleteSplits(String paragraph_id);

    @Insert("insert into `writing.split` (id,article_id,paragraph_id,`index`,start_index,end_index,deleted) values (#{id},#{article_id},#{paragraph_id},#{index},#{start_index},#{end_index},0)")
    void insertSplit(ParagraphSplit split);

    @Insert({
            "<script>",
            "insert into `writing.split` (id,article_id,paragraph_id,`index`,start_index,end_index,deleted)",
            "values ",
            "<foreach  collection='splits' item='split' separator=','>",
            "(#{split.id},#{split.article_id},#{split.paragraph_id},#{split.index},#{split.start_index},#{split.end_index},0)",
            "</foreach>",
            "</script>"
    })
    void insertSplits(@Param("splits") List<ParagraphSplit> splits);

    @Select("select * from `writing.split` where paragraph_id=#{paragraphId} and deleted=0")
    List<ParagraphSplit> selectSplitsByParagraphId(String paragraphId);

    @Select("select * from `writing.split` where id=#{id}")
    ParagraphSplit selectSplitById(String id);

    @Select("select * from `writing.split` where article_id=#{articleId} and deleted=0")
    List<ParagraphSplit> selectSplitsByArticleId(String articleId);

    @Insert("INSERT INTO `writing.recite` (`id`,`user_id`, `article_id`, `paragraph_id`, `split_id`, `use_time`, `score`, `content`, `submit_time`) VALUES " +
            "(#{id},#{user_id},#{article_id}, #{paragraph_id}, #{split_id}, #{use_time}, #{score}, #{content}, #{submit_time});")
    void insertSplitReciteRecord(ReciteRecord record);

    @Select("select * from `writing.recite` where user_id=#{user_id} and split_id=#{split_id}")
    List<ReciteRecord> selectSplitReciteHistory(@Param("user_id") String user_id, @Param("split_id") String split_id);

    @Select("select * from `writing.recite` where user_id=#{user_id} and article_id=#{article_id}")
    List<ReciteRecord> selectArticleReciteHistory(@Param("user_id") String user_id, @Param("article_id") String article_id);

    @Update("update `writing.recite` set score=#{score} where id=#{id}")
    void updateReciteScore(@Param("id") String id, @Param("score") float score);
}
