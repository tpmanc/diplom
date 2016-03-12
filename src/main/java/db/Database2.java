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
        ConfigDB config = (ConfigDB) ctx.getBean("dbConfig");
        // имя дравйвера
        bds.setDriverClassName(config.getDriverClassName());
        // url БД
        bds.setUrl(config.getDbUrl());
        // пользователь БД
        bds.setUsername(config.getDbUser());
        // пароль БД
        bds.setPassword(config.getDbPassword());
        // размер пула
        bds.setInitialSize(Integer.parseInt(config.getPoolSize()));
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
