package models;

import db.Database2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.acls.model.NotFoundException;

import java.sql.SQLException;
import java.util.*;

public class UserModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO user(id, phone, email, displayName, department, departmentNumber, fax, address) VALUES(:id, :phone, :email, :displayName, :department, :departmentNumber, :fax, :address)";
    private static final String updateById = "UPDATE user SET phone = :phone, email = :email, displayName = :displayName, department = :department, departmentNumber = :departmentNumber, fax = :fax, address = :address WHERE id = :id";
    private static final String getById = "SELECT * FROM user WHERE id = :id";
    private static final String getAllOnPage = "SELECT * FROM user LIMIT :limit OFFSET :offset";
    private static final String deleteById = "DELETE FROM property WHERE id = :id";
    private static final String getCount = "SELECT count(id) FROM user";

    private int id;
    private String phone;
    private String email;
    private String displayName;
    private String department;
    private String departmentNumber;
    private String fax;
    private String address;

    public static final int PAGE_COUNT = 10;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public UserModel(int id, String phone, String email, String displayName, String department, String departmentNumber, String fax, String address) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.displayName = displayName;
        this.department = department;
        this.departmentNumber = departmentNumber;
        this.fax = fax;
        this.address = address;
    }

    public UserModel(String phone, String email, String displayName) {
        this.phone = phone;
        this.email = email;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public static int getCount() {
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        return template.queryForObject(getCount, Integer.class);
    }

    public static ArrayList<UserModel> findAll(int limit, int offset) throws SQLException {
        ArrayList<UserModel> result = new ArrayList<UserModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAllOnPage, parameters);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String phone = (String) row.get("phone");
            String email = (String) row.get("email");
            String displayName = (String) row.get("displayName");
            String department = (String) row.get("department");
            String departmentNumber = (String) row.get("departmentNumber");
            String fax = (String) row.get("fax");
            String address = (String) row.get("address");
            result.add(new UserModel(modelId, phone, email, displayName, department, departmentNumber, fax, address));
        }
        return result;
    }

    public static UserModel findById(int id) {
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
            String department = (String) result.get("department");
            String departmentNumber = (String) result.get("departmentNumber");
            String fax = (String) result.get("fax");
            String address = (String) result.get("address");
            return new UserModel(userId, phone, email, displayName, department, departmentNumber, fax, address);
        } else {
            return null;
        }
    }

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            parameters.addValue("phone", phone);
            parameters.addValue("email", email);
            parameters.addValue("displayName", displayName);
            parameters.addValue("department", department);
            parameters.addValue("departmentNumber", departmentNumber);
            parameters.addValue("fax", fax);
            parameters.addValue("address", address);
            int rows = template.update(updateById, parameters);
            return rows > 0;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
