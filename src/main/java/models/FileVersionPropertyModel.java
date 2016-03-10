package models;

import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileVersionPropertyModel extends BaseModel implements ModelInterface {
    private static final String getById = "SELECT fileVersionProperty.id, fileVersionProperty.fileVersionId, property.title, fileVersionProperty.propertyId, fileVersionProperty.value FROM fileVersionProperty LEFT JOIN property ON property.id = fileVersionProperty.propertyId WHERE fileVersionProperty.id = :id";
    private static final String saveNew = "INSERT INTO fileVersionProperty(fileVersionId, propertyId, value) VALUES(:fileVersionId, :propertyId, :value)";
    private static final String getByFileVersion = "SELECT fileVersionProperty.id, property.title, fileVersionProperty.value FROM fileVersionProperty LEFT JOIN property ON fileVersionProperty.propertyId = property.id WHERE fileVersionId = :fileVersionId";
    private static final String deleteById = "DELETE FROM fileVersionProperty WHERE id = :id";

    private int id;
    private int fileVersionId;
    private int propertyId;
    private String value;
    private String title;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public boolean update() throws SQLException {
        // TODO
        return false;
    }

    public FileVersionPropertyModel() {
    }

    public FileVersionPropertyModel(int id, int fileVersionId, int propertyId, String value, String title) {
        this.id = id;
        this.fileVersionId = fileVersionId;
        this.propertyId = propertyId;
        this.value = value;
        this.title = title;
    }

    public static ArrayList getProperties(int fileVersionId) throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileVersionId", fileVersionId);
        List<Map<String, Object>> rows = template.queryForList(getByFileVersion, parameters);

        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", String.valueOf(row.get("id")));
            info.put("title", (String) row.get("title"));
            info.put("value", (String) row.get("value"));
            result.add(info);
        }
        return result;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("fileVersionId", fileVersionId);
            parameters.addValue("propertyId", propertyId);
            parameters.addValue("value", value);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return true;
        }
        return false;
    }

    public static FileVersionPropertyModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);
        for (Map row : rows) {
            Integer rowId = (Integer) row.get("id");
            Integer fileVersionId = (Integer) row.get("fileVersionId");
            Integer propertyId = (Integer) row.get("propertyId");
            String value = (String) row.get("value");
            String title = (String) row.get("title");
            return new FileVersionPropertyModel(rowId, fileVersionId, propertyId, value, title);
        }
        throw new CustomWebException("Свойство версии не найдено");
    }

    public boolean validate() {
        boolean isValid = true;
        // value
        List<String> valueErrors = new ArrayList<String>();
        if (value.length() > 255) {
            isValid = false;
            valueErrors.add("Значение свойства должно быть меньше 255 символов");
        }
        if (value.trim().length() == 0) {
            isValid = false;
            valueErrors.add("Заполните значение");
        }
        if (valueErrors.size() > 0) {
            errors.put("value", valueErrors);
        }

        // file id
        List<String> fileIdErrors = new ArrayList<String>();
        if (fileVersionId < 0) {
            isValid = false;
            fileIdErrors.add("Id файла должен быть >= 0");
        }
        if (fileIdErrors.size() > 0) {
            errors.put("fileId", fileIdErrors);
        }

        // property id
        List<String> propertyIdErrors = new ArrayList<String>();
        if (propertyId < 0) {
            isValid = false;
            propertyIdErrors.add("Id свойства должен быть >= 0");
        }
        if (propertyIdErrors.size() > 0) {
            errors.put("propertyId", propertyIdErrors);
        }

        return isValid;
    }

    public boolean delete() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    public int getId() {
        return id;
    }

    public int getFileVersionId() {
        return fileVersionId;
    }

    public void setFileVersionId(int fileVersionId) {
        this.fileVersionId = fileVersionId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
