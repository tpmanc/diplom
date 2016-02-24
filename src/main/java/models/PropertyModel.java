package models;

import db.Database2;
import exceptions.CustomSQLException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyModel extends BaseModel implements ModelInterface {
    private static HashMap<String, Integer> defaultProperties = new HashMap<String, Integer>();
    public static final int PRODUCT_NAME = 9;
    public static final int FILE_VERSION = 3;
    static {
        defaultProperties.put("FileDescription", 1);
        defaultProperties.put("OriginalFilename", 2);
        defaultProperties.put("FileVersion", FILE_VERSION);
        defaultProperties.put("ProductVersion", 4);
        defaultProperties.put("LegalCopyright", 5);
        defaultProperties.put("CompanyName", 6);
        defaultProperties.put("LegalTrademarks", 7);
        defaultProperties.put("InternalName", 8);
        defaultProperties.put("ProductName", PRODUCT_NAME);
        defaultProperties.put("Comments", 10);
    }

    private static final String saveNew = "INSERT INTO property(title) VALUES(:title)";
    private static final String getAll = "SELECT * FROM property";
    private static final String getById = "SELECT * FROM property WHERE id = :id";
    private static final String deleteById = "DELETE FROM property WHERE id = :id";
    private static final String updateById = "UPDATE property SET title = :title WHERE id = :id";
    private static final String duplicateCheck = "SELECT count(id) FROM property WHERE title = :title";

    private int id;
    private String title;

    public PropertyModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public PropertyModel(String title) {
        this.title = title;
    }

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public static int getDefaultProperty(String key) {
        if (defaultProperties.containsKey(key)) {
            return defaultProperties.get(key);
        } else {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        return queryAll(getAll);
    }

    public static PropertyModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<String> result = template.queryForList(getById, parameters, String.class);

        if (!result.isEmpty()) {
            int propertyId = Integer.parseInt(result.get(0));
            String title = result.get(1);
            return new PropertyModel(propertyId, title);
        } else {
            throw new CustomSQLException("Свойство не найдено");
        }
    }

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            parameters.addValue("title", title);
            int rows = template.update(updateById, parameters);
            if (rows > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("title", title);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = template.update(saveNew, parameters, keyHolder);
            if (rows > 0) {
                this.id = keyHolder.getKey().intValue();
                return true;
            }
        }
        return false;
    }

    /**
     * Валидация данных
     * @return Результат валидации
     */
    public boolean validate() {
        // title
        List<String> titleErrors = new ArrayList<String>();
        boolean result = true;
        if (title.length() > 255) {
            result = false;
            titleErrors.add("Название должно быть меньше 255 символов");
        }
        if (titleErrors.size() > 0) {
            errors.put("title", titleErrors);
        }

        List<String> duplicateErrors = new ArrayList<String>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", title);
        Integer count = template.queryForObject(duplicateCheck, parameters, Integer.class);
        if (count > 0) {
            result = true;
            duplicateErrors.add("Такое свойство уже существует");
        }
        if (titleErrors.size() > 0) {
            errors.put("duplicate", duplicateErrors);
        }

        return result;
    }

    public static boolean isRequired(int id) {
        for (Map.Entry<String, Integer> entry : defaultProperties.entrySet()) {
            if (entry.getValue().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean delete() throws SQLException {
        if (isRequired(id)) {
            return false;
        }
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }
}
