package models;

import db.Database2;
import exceptions.NotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilePropertyModel implements ModelInterface {
    private static final String updateById = "UPDATE fileProperty SET value = :value WHERE id = :id";
    private static final String saveNew = "INSERT INTO fileProperty(fileId, propertyId, value) VALUES(:fileId, :propertyId, :value)";
    private static final String getById = "SELECT fileProperty.id, fileProperty.fileId, property.title, fileProperty.propertyId, fileProperty.value FROM fileProperty LEFT JOIN property ON property.id = fileProperty.propertyId WHERE fileProperty.id = :id";
    private static final String getByFile = "SELECT fileProperty.*, property.title FROM fileProperty LEFT JOIN property ON fileProperty.propertyId = property.id WHERE fileId = :fileId AND propertyId <> :fileName AND propertyId <> :version";
    private static final String deleteById = "DELETE FROM fileProperty WHERE id = :id";
    private static final String checkIsExist = "SELECT * FROM fileProperty WHERE fileId = :fileId AND propertyId = :propertyId";

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
    public FilePropertyModel(int id, int fileId, int propertyId, String value) {
        this.id = id;
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

    public static ArrayList<FilePropertyModel> getProperties(int fileId) throws SQLException {
        ArrayList<FilePropertyModel> result = new ArrayList<FilePropertyModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", fileId);
        parameters.addValue("fileName", PropertyModel.PRODUCT_NAME);
        parameters.addValue("version", PropertyModel.FILE_VERSION);
        List<Map<String, Object>> rows = template.queryForList(getByFile, parameters);

        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            Integer modelFileId = (Integer) row.get("fileId");
            Integer propertyId = (Integer) row.get("propertyId");
            String title = (String) row.get("title");
            String value = (String) row.get("value");
            result.add(new FilePropertyModel(modelId, modelFileId, propertyId, value, title));
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
        throw new NotFoundException("Свойство файла не найдено");
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

    public static FilePropertyModel isPropertyExist(int fileId, int propertyId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", fileId);
        parameters.addValue("propertyId", propertyId);
        List<Map<String, Object>> rows = template.queryForList(checkIsExist, parameters);
        for (Map row : rows) {
            Integer itemId = (Integer) row.get("id");
            String value = (String) row.get("value");
            return new FilePropertyModel(itemId, fileId, propertyId, value);
        }
        return null;
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
            propertyIdErrors.add("Выберите свойство");
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
