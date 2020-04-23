package com.xcl.service;

import java.util.List;

/**
 * 面向接口编程
 * 会话对象
 */
public interface SqlSession {

    <E> List<E> selectList(String statementId,Object... params) throws Exception;

    //为Dao接口生成代理实现类
    public <T> T getMapper(Class<?> mapperClass);

}
