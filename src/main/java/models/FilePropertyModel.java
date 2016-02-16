package models;

import db.Database;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FilePropertyModel extends BaseModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO fileProperty(fileId, propertyId, value) VALUES(?, ?, ?)";
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
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveNew, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, fileId);
            ps.setInt(2, propertyId);
            ps.setString(3, value);
            int affectedRows = ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
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
