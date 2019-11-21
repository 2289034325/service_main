package com.acxca.ava.repository;

import com.acxca.components.spring.jwt.JwtUserDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserRepository {

    @Select({"select a.id,a.name,a.avatar,b.roles",
            "from `user.user` a",
            "inner join `user.credential` b on a.credential_id=b.id",
            "where b.username=#{username} and b.password=#{password}",
            "and b.enabled=1"})
    JwtUserDetail selectByUserNameAndPassword(@Param("username") String username, @Param("password") String password);

}
