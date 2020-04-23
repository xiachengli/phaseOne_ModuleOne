package com.xcl.service.imp;

import com.xcl.pojo.Configuration;
import com.xcl.pojo.MapperStatement;
import com.xcl.service.Executor;
import com.xcl.service.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    //数据库连接信息+sql信息
    private Configuration configuration;
    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId,Object... params) throws Exception {
        Executor executor = new SimpleExecutor();
        MapperStatement mapperStatement = configuration.getMapperStatements().get(statementId);
        List<E> list = executor.queryList(configuration,mapperStatement,params);
        return list;
    }

    /**
     * 利用反射生成动态代理类
     * @param cls
     * @param <T>
     * @return
     */
    public  <T> T getMapper(Class<?> mapperClass){
        Object proxy = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //获取方法所在类的全限定类名
                String className = method.getDeclaringClass().getName();
                //获取方法名
                String methodName = method.getName();
                //statementId == namespace.id == className.methodName
                String statementId = className+"."+methodName;
                List<Object> objects = selectList(statementId, args);
                return objects;
            }
        });
        return (T)proxy;

    }
}
