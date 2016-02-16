package models;

import db.Database;
import exceptions.CustomSQLException;
import org.springframework.security.acls.model.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyModel extends BaseModel implements ModelInterface {
    private static Map<String, Integer> defaultProperties = new HashMap<String, Integer>();
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
        // defaultProperties.put("Comments", 10);
    }

    private static final String saveNew = "INSERT INTO property(title) VALUES(?)";
    private static final String getAll = "SELECT * FROM property";
    private static final String getById = "SELECT * FROM property WHERE id = ?";

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
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getById);
        ps.setInt(1, id);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            int propertyId = res.getInt(1);
            String title = res.getString(2);
            return new PropertyModel(propertyId, title);
        } else {
            throw new CustomSQLException("Свойство не найдено");
        }
    }

    public boolean update() throws SQLException {
        // TODO
        return false;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveNew, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            int affectedRows = ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
            return true;
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

        // TODO проверка на дубликат свойства

        return result;
    }

    public boolean delete() throws SQLException {
        // TODO
        return false;
    }
}
