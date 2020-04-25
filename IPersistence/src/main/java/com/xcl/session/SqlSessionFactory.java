package com.xcl.session;

import com.xcl.pojo.Configuration;
import com.xcl.session.imp.DefaultSqlSession;
import com.xcl.util.XmlParserUtil;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * SqlSession会话对象工厂，用于生成SqlSession对象
 */
public class SqlSessionFactory {

    public SqlSession openSession() throws DocumentException, PropertyVetoException {
        //加载配置文件并把configuration对象交给sqlSession;
        Configuration configuration =  getConfiguration();
        SqlSession sqlSession = new DefaultSqlSession(configuration);
        return sqlSession;
    }

    private Configuration getConfiguration() throws DocumentException, PropertyVetoException {
        //加载配置文件到内存中,使用dom4j解析
        String name = "sqlConfig.xml";
        InputStream is = SqlSessionFactory.class.getClassLoader().getResourceAsStream(name);
        //组装configuration对象
        Configuration configuration = XmlParserUtil.parse(is);
        return configuration;
    }
}
