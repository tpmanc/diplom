package models;

import db.Database2;
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
    private static final String saveNew = "INSERT INTO fileVersionProperty(fileVersionId, propertyId, value) VALUES(:fileVersionId, :propertyId, :value)";
    private static final String getByFileVersion = "SELECT fileVersionProperty.id, property.title, fileVersionProperty.value FROM fileVersionProperty LEFT JOIN property ON fileVersionProperty.propertyId = property.id WHERE fileVersionId = :fileVersionId";

    private int id;
    private int fileVersionId;
    private int propertyId;
    private String value;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public boolean update() throws SQLException {
        // TODO
        return false;
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

    public boolean validate() {
        boolean isValid = true;
        // value
        List<String> valueErrors = new ArrayList<String>();
        if (value.length() > 255) {
            isValid = false;
            valueErrors.add("�������� �������� ������ ���� ������ 255 ��������");
        }
        if (value.trim().length() == 0) {
            isValid = false;
            valueErrors.add("��������� ��������");
        }
        if (valueErrors.size() > 0) {
            errors.put("value", valueErrors);
        }

        // file id
        List<String> fileIdErrors = new ArrayList<String>();
        if (fileVersionId < 0) {
            isValid = false;
            fileIdErrors.add("Id ����� ������ ���� >= 0");
        }
        if (fileIdErrors.size() > 0) {
            errors.put("fileId", fileIdErrors);
        }

        // property id
        List<String> propertyIdErrors = new ArrayList<String>();
        if (propertyId < 0) {
            isValid = false;
            propertyIdErrors.add("Id �������� ������ ���� >= 0");
        }
        if (propertyIdErrors.size() > 0) {
            errors.put("propertyId", propertyIdErrors);
        }

        return isValid;
    }

    public boolean delete() throws SQLException {
        // TODO
        return false;
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
