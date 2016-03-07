package models;

import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.*;

public class FileModel extends BaseModel implements ModelInterface {
    private static final String getById = "SELECT * FROM file WHERE id = :id";
    private static final String getByTitle = "SELECT * FROM file WHERE title = :title";
    private static final String saveNew = "INSERT INTO file(title) VALUES(:title)";
    private static final String getAll = "SELECT * FROM file";
    private static final String isFileExist = "SELECT count(id) FROM fileVersion WHERE hash = :hash AND fileSize = :fileSize";
    private static final String getCount = "SELECT count(id) FROM file";
    private static final String getTitles = "SELECT id, title FROM file WHERE title LIKE :str";

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

    public static ArrayList<HashMap> findTitles(String query){
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("str", "%" + query + "%");
        List<Map<String, Object>> rows = template.queryForList(getTitles, parameters);
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            String fileId = String.valueOf(row.get("id"));
            String title = (String) row.get("title");
            info.put("id", fileId);
            info.put("title", title);
            result.add(info);
        }
        return result;
    }

    public FileVersionModel getLastVersion() {
        String sql = "SELECT * FROM fileVersion WHERE fileId = :fileId ORDER BY version DESC LIMIT 1;";
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", id);
        List<Map<String, Object>> rows = template.queryForList(sql, parameters);
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            Integer fileId = (Integer) row.get("fileId");
            String version = (String) row.get("version");
            String hash = (String) row.get("hash");
            Long fileSize = (Long) row.get("fileSize");
            Long date = (Long) row.get("date");
            Boolean isFilled = ((Integer) row.get("isFilled") == 1);
            return  new FileVersionModel(id, fileId, version, hash, fileSize, date, isFilled);
        }
        throw new CustomWebException("Версия не найдена");
    }

    public boolean validate() {
        // title
        List<String> titleErrors = new ArrayList<String>();
        boolean isValid = true;
        if (title.length() > 255) {
            isValid = false;
            titleErrors.add("Название должно быть меньше 255 символов");
        }
        if (title.trim().length() == 0) {
            isValid = false;
            titleErrors.add("Заполните название");
        }
        if (titleErrors.size() > 0) {
            errors.put("title", titleErrors);
        }

        return isValid;
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
