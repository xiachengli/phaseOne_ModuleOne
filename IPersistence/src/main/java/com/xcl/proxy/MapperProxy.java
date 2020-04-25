package com.xcl.proxy;

import com.xcl.executor.Executor;
import com.xcl.pojo.MapperStatement;
import com.xcl.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * mapper接口代理类
 */
public class MapperProxy implements InvocationHandler {

    private Map<String, MapperStatement> mappers;
    private Executor executor;

    public MapperProxy(Map<String, MapperStatement> mappers,Executor executor) {
        this.mappers = mappers;
        this.executor = executor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取方法所在类的全限定类名
        String className = method.getDeclaringClass().getName();
        //获取方法名
        String methodName = method.getName();
        //statementId == namespace.id == className.methodName
        String statementId = className+"."+methodName;
        //获取MapperStatement对象
        MapperStatement mapperStatement = mappers.get(statementId);
        if (null == mapperStatement) {
            throw new IllegalArgumentException("参数异常");
        }
        String sqlCommandType = mapperStatement.getSqlCommand();
        switch (sqlCommandType){
            case "insert":
                return executor.insert(mapperStatement,args);
            case "delete":
                return executor.deleteById(mapperStatement,args);
            case "update":
                return executor.updateById(mapperStatement,args);
            case "select":
                return executor.selectList(mapperStatement,args);
                default:
                    System.out.println("非法操作");
                    throw new IllegalArgumentException("非法操作");
        }
    }
}