package models;

import db.Database;
import db.Database2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseModel {
    protected static JdbcTemplate jdbcTemplate;

    /**
     * Выборка всех записей из таблицы
     * @param query Запрос в БД на выборку
     */
    public static ArrayList<HashMap> queryAll(String query) throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(query);
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", String.valueOf(row.get("id")));
        }
//        Connection connection = Database.getConnection();
//        PreparedStatement ps = connection.prepareStatement(query);
//        ResultSet res = ps.executeQuery();
//        ResultSetMetaData rsmd = res.getMetaData();
//        int columnCount = rsmd.getColumnCount();
//        while (res.next()) {
//            HashMap<String, String> info = new HashMap<String, String>();
//            for (int i = 1; i <= columnCount; i++ ) {
//                String name = rsmd.getColumnName(i);
//                info.put(name, res.getString(i));
//            }
//            result.add(info);
//        }
        return result;
    }

    public static void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
