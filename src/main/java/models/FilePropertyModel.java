package models;

import db.Database;
import db.Database2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FilePropertyModel extends BaseModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO fileProperty(fileId, propertyId, value) VALUES(:fileId, :propertyId, :value)";
    private static final String getByFile = "SELECT fileProperty.id, property.title, fileProperty.value FROM fileProperty LEFT JOIN property ON fileProperty.propertyId = property.id WHERE fileId = ?";

    private int id;
    private int fileId;
    private int propertyId;
    private String value;

    public boolean update() throws SQLException {
        // TODO
        return false;
    }

    public static ArrayList getProperties(int fileId) throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getByFile);
        ps.setInt(1, fileId);
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", res.getString(1));
            info.put("title", res.getString(2));
            info.put("value", res.getString(3));
            result.add(info);
        }
        return result;
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
        // TODO
        return true;
    }

    public boolean delete() throws SQLException {
        // TODO
        return false;
    }

    public int getId() {
        return id;
    }

    public int getFileId() {
        return fileId;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
