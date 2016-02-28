package models;

import db.Database2;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class BaseModel {
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
            Iterator it = row.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                info.put(key, String.valueOf(row.get(key)));
            }
            result.add(info);
        }
        return result;
    }
}
