package db;


import helpers.ConfigDB;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 * Соединение с базой данных
 */
public class Database2 {
    private BasicDataSource bds = new BasicDataSource();

    private Database2() {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ConfigDB configDB = (ConfigDB) ctx.getBean("dbConfig");
        // имя дравйвера
        bds.setDriverClassName(configDB.getDriverClassName());
        // url БД
        bds.setUrl(configDB.getDbUrl());
        // пользователь БД
        bds.setUsername(configDB.getDbUser());
        // пароль БД
        bds.setPassword(configDB.getDbPassword());
        // размер пула
        bds.setInitialSize(Integer.parseInt(configDB.getPoolSize()));
        // свойства соединения
        bds.setConnectionProperties("useUnicode=yes;characterEncoding=utf8;");
    }

    private static class DataSourceHolder {
        private static final Database2 INSTANCE = new Database2();
    }

    public static Database2 getInstance() {
        return DataSourceHolder.INSTANCE;
    }

    public BasicDataSource getBds() {
        return bds;
    }

    public void setBds(BasicDataSource bds) {
        this.bds = bds;
    }
}
