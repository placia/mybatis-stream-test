package com.placia.spring5.mybatisstreamtest;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select id, name, age from user")
    List<User> findAll();

    @Delete("delete from user")
    void deleteAll();

    @Insert("insert into user (id, name, age) values (#{id}, #{name}, #{age})")
    void save(User user);
}
