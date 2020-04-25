package com.xcl.session.imp;

import com.xcl.executor.imp.SimpleExecutor;
import com.xcl.pojo.Configuration;
import com.xcl.pojo.MapperStatement;
import com.xcl.executor.Executor;
import com.xcl.proxy.MapperProxy;
import com.xcl.session.SqlSession;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    //数据库连接信息+sql信息
    private Configuration configuration;
    //执行器
    private Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new SimpleExecutor(configuration);
    }

    /**
     * 利用反射生成动态代理类
     * @param mapperClass
     * @param <T>
     * @return
     */
    public  <T> T getMapper(Class<?> mapperClass){
        MapperProxy mapperProxy = new MapperProxy(configuration.getMapperStatements(),this.executor);
        //类加载器，要代理的对象的接口，代理类
        Object proxy = Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass}, mapperProxy);
        return (T)proxy;

    }

    /**
     * 查询所有用户
     * @param statementId
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    @Override
    public <E> List<E> selectList(String statementId,Object... params) throws Exception {
        MapperStatement mapperStatement = configuration.getMapperStatements().get(statementId);
        if (null == mapperStatement) {
            throw new RuntimeException("the method-"+statementId+"-is not found");
        }
        List<E> list = executor.selectList(mapperStatement,params);
        return list;
    }

    /**
     * 根据用户id修改用户名
     * @param statementId
     * @param params
     * @return
     */
    @Override
    public int updateById(String statementId, Object... params) throws SQLException {
        MapperStatement mapperStatement = configuration.getMapperStatements().get(statementId);
        if (null == mapperStatement) {
            throw new RuntimeException("the method-"+statementId+"-is not found");
        }
        int result = executor.updateById(mapperStatement,params);
        return result;
    }


}

