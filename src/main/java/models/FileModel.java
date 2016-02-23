package models;

import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileModel extends BaseModel implements ModelInterface {
    private static final String getById = "SELECT * FROM file WHERE id = :id";
    private static final String saveNew = "INSERT INTO file(title) VALUES(:title)";
    private static final String getAll = "SELECT * FROM file";
    private static final String isFileExist = "SELECT count(id) FROM fileVersion WHERE hash = :hash AND fileSize = :fileSize";
    private static final String getCount = "SELECT count(id) FROM file";

    private int id;
    private String title;
    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public boolean update() throws SQLException {
        // TODO: update data
        return false;
    }

    public FileModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public FileModel() {
    }

    public static FileModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);
        for (Map row : rows) {
            Integer fileId = (Integer) row.get("id");
            String title = (String) row.get("title");
            return new FileModel(fileId, title);
        }
        throw new CustomWebException("Файл не найден");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        return queryAll(getAll);
    }

    public static int getCount() {
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        return template.queryForObject(getCount, Integer.class);
    }

    /**
     * Проверка на дублирование файла
     * Проверка осуществляется по хэшу и размеру файла
     * @return True если такой файл уже есть, иначе false
     */
    public static boolean isExist(String hash, long fileSize) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("hash", hash);
        parameters.addValue("fileSize", fileSize);
        Integer count = template.queryForObject(isFileExist, parameters, Integer.class);
        return count > 0;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("title", title);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return true;
        } else {
            return false;
        }
    }

    public boolean validate() {
        return true;
    }

    public boolean delete() throws SQLException {
        // TODO: file deleting
        return false;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
