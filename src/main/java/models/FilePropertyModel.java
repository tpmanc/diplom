package models;

import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilePropertyModel extends BaseModel implements ModelInterface {
    private static final String updateById = "UPDATE fileProperty SET value = :value WHERE id = :id";
    private static final String saveNew = "INSERT INTO fileProperty(fileId, propertyId, value) VALUES(:fileId, :propertyId, :value)";
    private static final String getById = "SELECT fileProperty.id, fileProperty.fileId, property.title, fileProperty.propertyId, fileProperty.value FROM fileProperty LEFT JOIN property ON property.id = fileProperty.propertyId WHERE fileProperty.id = :id";
    private static final String getByFile = "SELECT fileProperty.id, property.title, fileProperty.value FROM fileProperty LEFT JOIN property ON fileProperty.propertyId = property.id WHERE fileId = :fileId";
    private static final String deleteById = "DELETE FROM fileProperty WHERE id = :id";

    private int id;
    private int fileId;
    private int propertyId;
    private String title;
    private String value;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            parameters.addValue("value", value);
            int rows = template.update(updateById, parameters);
            if (rows > 0) {
                return true;
            }
        }
        return false;
    }

    public FilePropertyModel(int fileId, int propertyId, String value) {
        this.fileId = fileId;
        this.propertyId = propertyId;
        this.value = value;
    }

    public FilePropertyModel(int id, int fileId, int propertyId, String value, String title) {
        this.id = id;
        this.fileId = fileId;
        this.propertyId = propertyId;
        this.value = value;
        this.title = title;
    }

    public static ArrayList getProperties(int fileId) throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", fileId);
        List<Map<String, Object>> rows = template.queryForList(getByFile, parameters);

        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", String.valueOf(row.get("id")));
            info.put("title", (String) row.get("title"));
            info.put("value", (String) row.get("value"));
            result.add(info);
        }
        return result;
    }

    public static FilePropertyModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);
        for (Map row : rows) {
            Integer rowId = (Integer) row.get("id");
            Integer fileId = (Integer) row.get("fileId");
            Integer propertyId = (Integer) row.get("propertyId");
            String value = (String) row.get("value");
            String title = (String) row.get("title");
            return new FilePropertyModel(rowId, fileId, propertyId, value, title);
        }
        throw new CustomWebException("Свойство файла не найдено");
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("fileId", fileId);
            parameters.addValue("propertyId", propertyId);
            parameters.addValue("value", value);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return true;
        }
        return false;
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
        if (fileId < 0) {
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

    public int getFileId() {
        return fileId;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }
}
