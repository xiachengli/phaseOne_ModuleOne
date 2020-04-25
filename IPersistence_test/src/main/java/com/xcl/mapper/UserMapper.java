package com.xcl.mapper;

import com.xcl.pojo.User;

import java.util.List;

public interface UserMapper {

    List<User> selectList();

    int updateById(User user);

    int deleteById(User user);

    int insert(User user);
}
