package com.xcl.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xcl.pojo.Configuration;
import com.xcl.pojo.MapperStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParserUtil {
    /**
     * 根据配置文件解析configuration对象
     */
    public static Configuration parse(InputStream inputStream) throws DocumentException, PropertyVetoException {
        Configuration configuration = new Configuration();

        // parse start-----------------------------------------
        Document document = new SAXReader().read(inputStream);
        Element root = document.getRootElement();
        List<Element> propertys = root.selectNodes("//property");
        Map<String,String> map = new HashMap<>();
        for (Element e : propertys) {
            String name = e.attributeValue("name");
            String value = e.attributeValue("value");
            map.put(name,value);
        }
        //设置数据源
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(map.get("driverClass"));
        dataSource.setJdbcUrl(map.get("jdbcUrl"));
        dataSource.setUser(map.get("username"));
        dataSource.setPassword(map.get("password"));
        //封装数据
        configuration.setDataSource(dataSource);
        List<Element> mappers = root.selectNodes("//mapper");
        parseMapper(configuration,mappers);
        //parse end---------------------------------------------

        return configuration;
    }

    /**
     * 解析sql语句
     * @param mappers
     * @return
     */
    private static void parseMapper(Configuration configuration,List<Element> mappers) throws DocumentException {
        for (Element e : mappers) {
            System.out.println(e);
            String pathName = e.attributeValue("location");
            System.out.println(pathName);
            try {
                InputStream is = XmlParserUtil.class.getClassLoader().getResourceAsStream(pathName);
               // InputStream is = new FileInputStream(new File(pathName));
                Document document = new SAXReader().read(is);
                Element root = document.getRootElement();
                //命名空间
                String namespace = root.attributeValue("namespace");

                List<Element> selectList = root.selectNodes("//select");
                for (Element element : selectList) {
                    //id
                    String id = element.attributeValue("id");
                    //返回值类型
                    String resultType = element.attributeValue("resultType");
                    //参数类型
                    String parameterType = element.attributeValue("parameterType");
                    //sql语句
                    String sql = element.getTextTrim();

                    MapperStatement mapperStatement = new MapperStatement();
                    mapperStatement.setId(id);
                    mapperStatement.setResultType(resultType);
                    mapperStatement.setParameterType(parameterType);
                    mapperStatement.setSql(sql);

                    //sql语句的唯一标识
                    String statementId = namespace+"."+id;

                    configuration.getMapperStatements().put(statementId,mapperStatement);
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
