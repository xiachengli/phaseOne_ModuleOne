package com.xcl.service.imp;

import com.xcl.pojo.Configuration;
import com.xcl.pojo.MapperStatement;
import com.xcl.service.Executor;
import com.xcl.util.GenericTokenParser;
import com.xcl.util.ParameterMapping;
import com.xcl.util.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 底层执行jdbc代码
 */
public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> queryList(Configuration configuration, MapperStatement mapperStatement, Object... params){
        //结果集
        List<E> results = new ArrayList<>();
        try {
            //获取连接
            Connection connnection = configuration.getDataSource().getConnection();
            //sql语句
            String sql = mapperStatement.getSql();

            //将sql解析为jdbc认识的格式：jdbc支持?占位符，而我们自定义使用#{}
            Map<String,Object> boundSql = getBoundSql(sql);
            sql = (String)boundSql.get("sql");
            List<ParameterMapping> parameterMappings = (List<ParameterMapping>)boundSql.get("parameterMappings");

            PreparedStatement preparedStatement = connnection.prepareStatement(sql);

            //通过参数类型(约定：写类的全限定名以便通过反射获取Class信息)获取参数类型的Class
            String  parameterType = mapperStatement.getParameterType();
            Class parameterClass = Class.forName(parameterType);
            //设置preparedStatement参数
            for (int i=0; i<parameterMappings.size(); i++) {
                ParameterMapping temp = parameterMappings.get(i);
                //反射：获取类的指定字段
                Field declaredField = parameterClass.getDeclaredField(temp.getContent());
                //取消安全检查，以便访问私有属性或方法
                declaredField.setAccessible(true);

                //我不是很理解这个params[0]
                Object value = declaredField.get(params[0]);

                preparedStatement.setObject(i+1,value);

            }

            ResultSet resultSet = preparedStatement.executeQuery();
            //通过返回值类型(约定：写类的全限定名以便通过反射获取Class信息)获取返回类型的Class
            String resultType = mapperStatement.getResultType();
            Class resultClass = Class.forName(resultType);

            //结果集
            results = getResult(resultSet,resultClass);

            } catch (Exception e) {
              e.printStackTrace();
        }
        return results;
    }

    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private Map<String,Object> getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);

        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        Map<String,Object> result = new HashMap<>();
        result.put("sql",parseSql);
        result.put("parameterMappings",parameterMappings);
        return result;

    }

    private <E> List<E> getResult(ResultSet resultSet,Class resultClass) throws Exception{
        ArrayList<Object> objects = new ArrayList<>();
        while (resultSet.next()) {
            //调用类的无参构造
            Object o = resultClass.getDeclaredConstructor().newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {

                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);

            }
            objects.add(o);
        }
        return (List<E>)objects;
    }

}
