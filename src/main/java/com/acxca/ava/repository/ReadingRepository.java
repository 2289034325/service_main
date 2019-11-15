package com.acxca.ava.repository;

import com.acxca.ava.entity.BookMark;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ReadingRepository {

    @Select("select * from reading_book_mark where user_id=#{user_id} and deleted=0")
    List<BookMark> selectBookMarks(@Param("user_id") String user_id);

    @Insert("insert into reading_book_mark (user_id,name,title,url,time,deleted) values(#{user_id},#{name},#{title},#{url},#{time},0)")
    @SelectKey(statement="select @@identity", keyProperty="id", before=false, resultType=Integer.class)
    Integer insertBookMark(BookMark bookMark);

    @Update("update reading_book_mark set name=#{name}, title=#{title}, url = #{url}, time=#{time} where id = #{id}")
    void updateBookMark(BookMark bookMark);

    @Update("update reading_book_mark set deleted = 1 where id = #{id}")
    void deleteBookMark(@Param("id") String id);
}
