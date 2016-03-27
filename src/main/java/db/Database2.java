package db;


import config.Settings;
import models.helpers.DatabaseSettings;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Соединение с базой данных
 */
public class Database2 {
    private BasicDataSource bds = new BasicDataSource();

    private Database2() {
        Settings settings = new Settings();
        DatabaseSettings databaseSettings = settings.getDatabaseSettings();
        // имя дравйвера
        bds.setDriverClassName(databaseSettings.getDriver());
        // url БД
        bds.setUrl(databaseSettings.getUrl());
        // пользователь БД
        bds.setUsername(databaseSettings.getUser());
        // пароль БД
        bds.setPassword(databaseSettings.getPassword());
        // размер пула
        bds.setInitialSize(5);
//        bds.setInitialSize(Integer.parseInt(config.getPoolSize()));
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
