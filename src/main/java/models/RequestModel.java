package models;

import db.Database2;
import exceptions.NotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO request(userId, text, status, date, comment) VALUES(:userId, :text, :status, :date, :comment)";
    private static final String getAll = "SELECT * FROM request ORDER BY date DESC LIMIT :limit OFFSET :offset";
    private static final String getAllByUser = "SELECT * FROM request WHERE userId = :userId ORDER BY date DESC LIMIT :limit OFFSET :offset";
    private static final String getCountByUser = "SELECT count(id) FROM request WHERE userId = :userId";
    private static final String getCount = "SELECT count(id) FROM request";
    private static final String getById = "SELECT * FROM request WHERE id = :id ORDER BY date DESC";
    private static final String getFiles = "SELECT * FROM requestFile WHERE requestId = :requestId";
    private static final String deleteById = "DELETE FROM request WHERE id = :id";
    private static final String getNewCount = "SELECT count(id) FROM request WHERE status = :status AND userId = :userId";
    private static final String updateById = "UPDATE request SET status = :status, comment = :comment WHERE id = :id";

    public final static int NEW = 1;
    public final static int ACCEPTED = 2;
    public final static int CANCELED = 3;

    public static final int PAGE_COUNT = 10;

    private int id;
    private int userId;
    private String text;
    private int status;
    private long date;
    private String comment;

    public RequestModel() {}
    public RequestModel(int id, int userId, String text, int status, long date, String comment) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.status = status;
        this.date = date;
        this.comment = comment;
    }

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            parameters.addValue("comment", comment);
            parameters.addValue("status", status);
            int rows = template.update(updateById, parameters);
            return rows > 0;
        }
        return false;
    }

    public boolean add() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("text", text);
            parameters.addValue("date", date);
            parameters.addValue("status", status);
            parameters.addValue("userId", userId);
            if (comment == null) {
                parameters.addValue("comment", "");
            } else {
                parameters.addValue("comment", comment);
            }
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = template.update(saveNew, parameters, keyHolder);
            if (rows > 0) {
                this.id = keyHolder.getKey().intValue();
                return true;
            }
        }
        return false;
    }

    public boolean validate() {
        // nothing
        return true;
    }

    public boolean delete() throws SQLException {
        ArrayList<RequestFileModel> files = getFiles();
        for (RequestFileModel file : files) {
            file.delete();
        }
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    public static RequestModel findById(int id) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int requestId = (Integer) result.get("id");
            int userId = (Integer) result.get("userId");
            String text = (String) result.get("text");
            int status = (Integer) result.get("status");
            long date = (Long) result.get("date");
            String comment = (String) result.get("comment");
            return new RequestModel(requestId, userId, text, status, date, comment);
        } else {
            throw new NotFoundException("Свойство не найдено");
        }
    }

    public static ArrayList<RequestModel> findAll(int limit, int offset) {
        ArrayList<RequestModel> result = new ArrayList<RequestModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAll, parameters);
        for (Map row : rows) {
            Integer reqId = (Integer) row.get("id");
            Integer userId = (Integer) row.get("userId");
            String text = (String) row.get("text");
            Integer status = (Integer) row.get("status");
            Long date = (Long) row.get("date");
            String comment = (String) row.get("comment");
            result.add(new RequestModel(reqId, userId, text, status, date, comment));
        }
        return result;
    }

    public ArrayList<RequestFileModel> getFiles() {
        ArrayList<RequestFileModel> files = new ArrayList<RequestFileModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("requestId", id);
        List<Map<String, Object>> rows = template.queryForList(getFiles, parameters);
        for (Map<String, Object> row : rows) {
            int fileId = (Integer) row.get("id");
            String hash = (String) row.get("hash");
            String fileName = (String) row.get("fileName");
            String extension = (String) row.get("extension");
            long fileSize = (Long) row.get("fileSize");
            files.add(new RequestFileModel(fileId, id, hash, fileName, extension, fileSize));
        }
        return files;
    }

    public static ArrayList<RequestModel> findAllByUser(int userId, int limit, int offset) {
        ArrayList<RequestModel> result = new ArrayList<RequestModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAllByUser, parameters);
        for (Map row : rows) {
            Integer reqId = (Integer) row.get("id");
            String text = (String) row.get("text");
            Integer status = (Integer) row.get("status");
            Long date = (Long) row.get("date");
            String comment = (String) row.get("comment");
            result.add(new RequestModel(reqId, userId, text, status, date, comment));
        }
        return result;
    }

    public static int getCountForUser(int userId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        return template.queryForObject(getCountByUser, parameters, Integer.class);
    }

    public static int getCount() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        return template.queryForObject(getCount, parameters, Integer.class);
    }

    public static int getNewCountForUser(int userId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("status", NEW);
        return template.queryForObject(getNewCount, parameters, Integer.class);
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
