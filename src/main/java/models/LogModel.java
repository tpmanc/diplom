package models;

import db.Database2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Модель лога
 */
public class LogModel extends BaseModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO log(userId, date, level, message) VALUES (:userId, :date, :level, :message)";
    private static final String deleteById = "DELETE FROM log WHERE id = :id";
    private static final String getAll = "SELECT * FROM log LIMIT :limit OFFSET :offset";

    public final static String INFO = "info";
    public final static String WARNING = "warning";
    public final static String ERROR = "error";

    private int id;
    private int userId;
    private long date;
    private String level;
    private String message;

    public LogModel() {}

    public LogModel(int id, int userId, long date, String level, String message) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.level = level;
        this.message = message;
    }

    public boolean update() throws SQLException {
        // nothing
        return false;
    }

    public static void addInfo(int userId, String message) {
        addMessage(userId, INFO, message);
    }
    public static void addWarning(int userId, String message) {
        addMessage(userId, WARNING, message);
    }
    public static void addError(int userId, String message) {
        addMessage(userId, ERROR, message);
    }

    private static void addMessage(int userId, String level, String message) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        long time = new Date().getTime();
        parameters.addValue("date", time);
        parameters.addValue("level", level);
        parameters.addValue("message", message);
        template.update(saveNew, parameters);
    }

    public boolean add() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("userId", userId);
            parameters.addValue("date", date);
            parameters.addValue("level", level);
            parameters.addValue("message", message);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return true;
        }
        return false;
    }

    public boolean validate() {
        return true;
    }

    public boolean delete() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    public static ArrayList<LogModel> findAll(int limit, int offset) throws SQLException {
        ArrayList<LogModel> result = new ArrayList<LogModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAll, parameters);
        for (Map<String, Object> row : rows) {
            Integer logId = (Integer) row.get("id");
            Integer userId = (Integer) row.get("userId");
            Long date = (Long) row.get("date");
            String level = (String) row.get("level");
            String message = (String) row.get("message");
            result.add(new LogModel(logId, userId, date, level, message));
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
