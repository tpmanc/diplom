package models;

import db.Database2;
import exceptions.CustomSQLException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.*;

public class UserModel extends BaseModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO user(id, phone, email, displayName) VALUES(:id, :phone, :email, :displayName)";
    private static final String getById = "SELECT * FROM user WHERE id = :id";
    private static final String getAllOnPage = "SELECT * FROM user LIMIT :limit OFFSET :offset";
    private static final String deleteById = "DELETE FROM property WHERE id = :id";

    private int id;
    private String phone;
    private String email;
    private String displayName;

    public static final int PAGE_COUNT = 10;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public UserModel(int id, String phone, String email, String displayName) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.displayName = displayName;
    }

    public UserModel(String phone, String email, String displayName) {
        this.phone = phone;
        this.email = email;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    /**
     * Проверка, существует ли в БД уже такой пользователь
     * @param id employeeId пользователя
     * @return boolean
     */
    public static UserModel isExist(int id) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int userId = (Integer) result.get("id");
            String phone = (String) result.get("phone");
            String email = (String) result.get("email");
            String displayName = (String) result.get("displayName");
            return new UserModel(userId, phone, email, displayName);
        } else {
            return null;
        }
    }

    public static ArrayList<HashMap> findAll(int limit, int offset) throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAllOnPage, parameters);
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", String.valueOf(row.get("id")));
            info.put("displayName", String.valueOf(row.get("displayName")));
            result.add(info);
        }
        return result;
    }

    public static UserModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int userId = (Integer) result.get("id");
            String phone = (String) result.get("phone");
            String email = (String) result.get("email");
            String displayName = (String) result.get("displayName");
            return new UserModel(userId, phone, email, displayName);
        } else {
            throw new CustomSQLException("Пользователь не найден");
        }
    }

    public boolean update() throws SQLException {
        if (validate()) {
            // TODO
        }
        return false;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            parameters.addValue("phone", phone);
            parameters.addValue("email", email);
            parameters.addValue("displayName", displayName);
            int rows = template.update(saveNew, parameters);
            if (rows > 0) {
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
        // TODO
        return true;
    }

    public boolean delete() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
