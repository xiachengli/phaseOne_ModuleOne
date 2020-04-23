package com.xcl.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置信息
 */
public class Configuration {

    //数据源：包含数据库连接的相关信息
    private DataSource dataSource;

    //mapperStatement
    private Map<String,MapperStatement> mapperStatements = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MapperStatement> getMapperStatements() {
        return mapperStatements;
    }

    public void setMapperStatements(Map<String, MapperStatement> mapperStatements) {
        this.mapperStatements = mapperStatements;
    }


}
