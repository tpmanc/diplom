package models;

import db.Database;
import org.springframework.security.acls.model.NotFoundException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserModel {
    private static final String getById = "SELECT users.*, userRole.role FROM users LEFT JOIN userRole ON userRole.userId = users.id WHERE users.id = ?";
    private static final String saveById = "UPDATE users SET username = ?, isEnabled = ? WHERE id = ?";
    private static final String getAll = "SELECT * FROM users";

    private int id;
    private String username;
    private String password;
    private boolean isEnabled;
    private String role;

    public static UserModel findById(int id) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getById);
        ps.setInt(1, id);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            int userId = res.getInt(1);
            String  username = res.getString(2);
            String password = res.getString(3);
            boolean isEnabled = res.getBoolean(4);
            String role = res.getString(5);
            return new UserModel(userId, username, password, isEnabled, role);
        }
        throw new NotFoundException("User not found");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getAll);
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            HashMap<String, String> userInfo = new HashMap<String, String>();
            userInfo.put("id", res.getString(1));
            userInfo.put("username", res.getString(2));
            userInfo.put("password", res.getString(3));
            userInfo.put("isEnabled", res.getString(4));
            result.add(userInfo);
        }
        return result;
    }

    public UserModel(int id, String username, String password, boolean isEnabled, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isEnabled = isEnabled;
        this.role = role;
    }

    public boolean save() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(saveById);
        ps.setString(1, username);
        ps.setBoolean(2, isEnabled);
        ps.setInt(3, id);
        return ps.execute();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
