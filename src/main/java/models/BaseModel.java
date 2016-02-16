package models;

import db.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseModel {
    /**
     * Выборка всех записей из таблицы
     * @param query Запрос в БД на выборку
     */
    public static ArrayList<HashMap> queryAll(String query) throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet res = ps.executeQuery();
        ResultSetMetaData rsmd = res.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (res.next()) {
            HashMap<String, String> info = new HashMap<String, String>();
            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                info.put(name, res.getString(i));
            }
            result.add(info);
        }
        return result;
    }
}
