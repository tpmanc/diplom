package models;

import db.Database;
import exceptions.CustomWebException;
import org.springframework.security.acls.model.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryModel extends BaseModel implements ModelInterface {
    private static final String getById = "SELECT * FROM category WHERE id = ?";
    private static final String saveById = "UPDATE category SET title = ?, isEnabled = ? WHERE id = ?";
    private static final String saveNew = "INSERT INTO category(title, isEnabled) VALUES(?, ?)";
    private static final String getAll = "SELECT * FROM category";
    private static final String deleteById = "DELETE FROM category WHERE id = ?";

    private int id;
    private String title;
    private boolean isEnabled;
    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public static CategoryModel findById(int id) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getById);
        ps.setInt(1, id);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            int categoryId = res.getInt(1);
            String title = res.getString(2);
            boolean isEnabled = res.getBoolean(3);
            return new CategoryModel(categoryId, title, isEnabled);
        }
        throw new CustomWebException("User not found");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        return queryAll(getAll);
    }

    public CategoryModel(int id, String title, boolean isEnabled) {
        this.id = id;
        this.title = title;
        this.isEnabled = isEnabled;
    }

    public CategoryModel(String title, boolean isEnabled) {
        this.title = title;
        this.isEnabled = isEnabled;
    }

    public boolean update() throws SQLException {
        if (this.validate()) {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveById);
            ps.setString(1, title);
            ps.setBoolean(2, isEnabled);
            ps.setInt(3, id);
            ps.execute();
            return true;
        } else {
            return false;
        }
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveNew , Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setBoolean(2, isEnabled);
            int affectedRows = ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean delete() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(deleteById);
        ps.setInt(1, id);
        ps.execute();
        return true;
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

        return result;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
