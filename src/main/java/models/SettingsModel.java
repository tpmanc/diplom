package models;

import db.Database2;
import exceptions.NotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Модель шаблонов экспорта
 */
public class SettingsModel implements ModelInterface {
    private static final String getById = "SELECT * FROM settings WHERE id = :id";
    private static final String saveNew = "INSERT INTO settings(id, value) VALUES (:id, :value)";

    public static final int UPLOAD_PATH = 1;
    public static final int UPLOAD_REQUEST_PATH = 2;

    private int id;
    private String value;

    public SettingsModel(int id) {
        this.id = id;
        this.value = "";
    }

    public SettingsModel(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public boolean update() throws SQLException {
        // todo
        return false;
    }

    public boolean add() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource queryParameters = new MapSqlParameterSource();
            queryParameters.addValue("id", id);
            queryParameters.addValue("value", value);
            template.update(saveNew, queryParameters);
            return id > 0;
        }
        return false;
    }

    public boolean validate() {
        return true;
    }

    public boolean delete() throws SQLException {
        // nothing
        return false;
    }

    public static SettingsModel findById(int id) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int settingId = (Integer) result.get("id");
            String value = (String) result.get("value");
            return new SettingsModel(settingId, value);
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
