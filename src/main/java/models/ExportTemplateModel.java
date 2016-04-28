package models;

import db.Database2;
import exceptions.NotFoundException;
import models.helpers.LogOutput;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Модель шаблонов экспорта
 */
public class ExportTemplateModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO exportTemplate(title, parameters, finalCommands) VALUES (:title, :parameters, :finalCommands)";
    private static final String getAll = "SELECT * FROM exportTemplate";
    private static final String getById = "SELECT * FROM exportTemplate WHERE id = :id";
    private static final String deleteById = "DELETE FROM exportTemplate WHERE id = :id";
    private static final String updateById = "UPDATE exportTemplate SET parameters = :parameters, finalCommands = :finalCommands WHERE id = :id";

    private int id;
    private String title;
    private String parameters;
    private String finalCommands;

    public ExportTemplateModel() {}

    public ExportTemplateModel(int id, String title, String parameters, String finalCommands) {
        this.id = id;
        this.title = title;
        this.parameters = parameters;
        this.finalCommands = finalCommands;
    }

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource queryParameters = new MapSqlParameterSource();
            queryParameters.addValue("id", id);
            queryParameters.addValue("parameters", parameters);
            queryParameters.addValue("finalCommands", finalCommands);
            int rows = template.update(updateById, queryParameters);
            if (rows > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean add() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource queryParameters = new MapSqlParameterSource();
            queryParameters.addValue("title", title);
            queryParameters.addValue("parameters", parameters);
            queryParameters.addValue("finalCommands", finalCommands);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, queryParameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return id > 0;
        }
        return false;
    }

    public boolean validate() {
        return true;
    }

    public boolean delete() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    public static ExportTemplateModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int templateId = (Integer) result.get("id");
            String title = (String) result.get("title");
            String params = (String) result.get("parameters");
            String finalCommands = (String) result.get("finalCommands");
            return new ExportTemplateModel(templateId, title, params, finalCommands);
        }
        throw new NotFoundException("Шаблон не найден", "404");
    }

    public static ArrayList<ExportTemplateModel> findAll() throws SQLException {
        ArrayList<ExportTemplateModel> result = new ArrayList<ExportTemplateModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        rows = template.queryForList(getAll, parameters);
        for (Map<String, Object> row : rows) {
            Integer templateId = (Integer) row.get("id");
            String title = (String) row.get("title");
            String params = (String) row.get("parameters");
            String finalCommands = (String) row.get("finalCommands");
            result.add(new ExportTemplateModel(templateId, title, params, finalCommands));
        }
        return result;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getFinalCommands() {
        return finalCommands;
    }

    public void setFinalCommands(String finalCommands) {
        this.finalCommands = finalCommands;
    }
}
