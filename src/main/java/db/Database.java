package db;


import exceptions.CustomSQLException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    public static Connection getConnection() {
//        Properties prop = new Properties();
        try {
//            FileInputStream in = new FileInputStream("WEB-INF/connection.properties");
//            prop.load(new FileInputStream(System.getProperty("WEB-INF/connection.properties")));
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/repository", "root", "");
//            return DriverManager.getConnection(
//                    prop.getProperty("DB_URL") +
//                    prop.getProperty("DB_NAME") +
//                    "?characterEncoding=" + prop.getProperty("DB_CHARCODE"),
//                    prop.getProperty("DB_USERNAME"), prop.getProperty("DB_PASSWORD"));
        } catch (SQLException e) {
            throw new CustomSQLException("Нет соединения с базой данных");
        }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            throw new CustomSQLException("Ошибка при чтении файла с конфигурацией");
//        }
    }
}
