package com.xcl.service;

import com.xcl.pojo.Configuration;
import com.xcl.pojo.MapperStatement;

import java.util.List;

public interface Executor {

    <E> List<E> queryList(Configuration configuration, MapperStatement mapperStatement, Object... params) throws ClassNotFoundException;
}
