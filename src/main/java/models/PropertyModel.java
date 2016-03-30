package models;

import db.Database2;
import exceptions.NotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.*;

public class PropertyModel implements ModelInterface {
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
    private static final String getAllCustom = "SELECT * FROM property WHERE id > 10";
    private static final String getAllCustomByPage = "SELECT * FROM property WHERE id > 10 LIMIT :limit OFFSET :offset";
    private static final String getCount = "SELECT count(id) FROM property WHERE id > 10";
    private static final String getById = "SELECT * FROM property WHERE id = :id";
    private static final String deleteById = "DELETE FROM property WHERE id = :id";
    private static final String updateById = "UPDATE property SET title = :title WHERE id = :id";
    private static final String duplicateCheck = "SELECT count(id) FROM property WHERE title = :title";
    private static final String getFileNotUsed = "SELECT * FROM property WHERE id <> 3 AND id <> 10 AND id NOT IN (SELECT propertyId FROM fileProperty WHERE fileId = :fileId);";
    private static final String getVersionNotUsed = "SELECT * FROM property WHERE id <> 3 AND id <> 10 AND id NOT IN ( SELECT propertyId FROM fileVersionProperty WHERE fileVersionId = :fileId );";

    private int id;
    private String title;
    private boolean isCustom = false;

    public final static int PAGE_COUNT = 10;

    public PropertyModel(int id, String title, boolean isCustom) {
        this.id = id;
        this.title = title;
        this.isCustom = isCustom;
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

    public static ArrayList<PropertyModel> findAll(int limit, int offset) throws SQLException {
        ArrayList<PropertyModel> result = new ArrayList<PropertyModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAllCustomByPage, parameters);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            boolean isCustom =!isRequired(modelId);
            result.add(new PropertyModel(modelId, title, isCustom));
        }
        return result;
    }
    public static ArrayList<PropertyModel> findAll() throws SQLException {
        ArrayList<PropertyModel> result = new ArrayList<PropertyModel>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(getAllCustom);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            boolean isCustom =!isRequired(modelId);
            result.add(new PropertyModel(modelId, title, isCustom));
        }
        return result;
    }

    public static int getCount() {
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        return template.queryForObject(getCount, Integer.class);
    }

    public static ArrayList<PropertyModel> findAllCustom() throws SQLException {
        ArrayList<PropertyModel> result = new ArrayList<PropertyModel>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(getAllCustom);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            boolean isCustom =!isRequired(modelId);
            result.add(new PropertyModel(modelId, title, isCustom));
        }
        return result;
    }

    /**
     * Получить список свойств, за исключением названия и версии, которые еще не назначены
     * @throws SQLException
     */
    public static ArrayList<PropertyModel> findAllNotUsedCustom(int fileId, boolean isVersion) throws SQLException {
        String query;
        if (isVersion) {
            query = getVersionNotUsed;
        } else {
            query = getFileNotUsed;
        }
        ArrayList<PropertyModel> result = new ArrayList<PropertyModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", fileId);
        List<Map<String, Object>> rows = template.queryForList(query, parameters);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            boolean isCustom = !isRequired(modelId);
            result.add(new PropertyModel(modelId, title, isCustom));
        }
        return result;
    }

    public static String getAllJson() throws SQLException {
        ArrayList<PropertyModel> arr = findAll();
        JSONArray res = new JSONArray();
        for (PropertyModel row : arr) {
            JSONObject one = new JSONObject();
            one.put("propertyId", row.getId());
            one.put("title", row.getTitle());
            res.add(one);
        }
        return res.toJSONString();
    }

    public static PropertyModel findById(int id) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int modelId = (Integer) result.get("id");
            String title = (String) result.get("title");
            boolean isCustom =!isRequired(modelId);
            return new PropertyModel(modelId, title, isCustom);
        } else {
            throw new NotFoundException("Свойство не найдено");
        }
    }

    public static PropertyModel findCustomById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int propertyId = (Integer) result.get("id");
            String title = (String) result.get("title");
            if (!isRequired(propertyId)) {
                return new PropertyModel(propertyId, title, true);
            }
        }
        throw new NotFoundException("Свойство не найдено", "404");
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
        if (title.trim().length() == 0) {
            result = false;
            titleErrors.add("Заполните название");
        }

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", title);
        Integer count = template.queryForObject(duplicateCheck, parameters, Integer.class);
        if (count > 0) {
            result = false;
            titleErrors.add("Такое свойство уже существует");
        }
        if (titleErrors.size() > 0) {
            errors.put("title", titleErrors);
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

    public boolean isCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }
}
