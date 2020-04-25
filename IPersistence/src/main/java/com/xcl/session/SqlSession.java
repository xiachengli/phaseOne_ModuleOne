package com.xcl.session;

import java.sql.SQLException;
import java.util.List;

/**
 * 面向接口编程
 * 会话对象
 */
public interface SqlSession {

    /**
     * 为Dao接口生成代理实现类
     */
    <T> T getMapper(Class<?> mapperClass);

    /**
     * 查询所有
     * @param statementId
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    <E> List<E> selectList(String statementId,Object... params) throws Exception;

    int updateById(String statementId,Object... params) throws SQLException;



}
