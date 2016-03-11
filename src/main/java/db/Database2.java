package db;


import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Соединение с базой данных
 */
public class Database2 {
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/repository";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final int CONN_POOL_SIZE = 5;

    private BasicDataSource bds = new BasicDataSource();

    private Database2() {
        // имя дравйвера
        bds.setDriverClassName(DRIVER_CLASS_NAME);
        // url БД
        bds.setUrl(DB_URL);
        // пользователь БД
        bds.setUsername(DB_USER);
        // пароль БД
        bds.setPassword(DB_PASSWORD);
        // размер пула
        bds.setInitialSize(CONN_POOL_SIZE);
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
