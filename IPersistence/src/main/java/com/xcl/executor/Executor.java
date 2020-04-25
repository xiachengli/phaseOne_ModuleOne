package com.xcl.executor;

import com.xcl.pojo.MapperStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> selectList(MapperStatement mapperStatement, Object... params) throws ClassNotFoundException;

    int updateById(MapperStatement mapperStatement, Object[] params) throws SQLException;

    int deleteById(MapperStatement mapperStatement, Object[] params) throws SQLException;

    int insert(MapperStatement mapperStatement, Object[] params) throws SQLException;
}
