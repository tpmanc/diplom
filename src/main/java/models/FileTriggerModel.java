package models;

import db.Database2;
import exceptions.NotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Модель шаблонов экспорта
 */
public class FileTriggerModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO fileTrigger(extension, command, `regexp`) VALUES (:extension, :command, :regexp)";
    private static final String getAll = "SELECT * FROM fileTrigger";
    private static final String getById = "SELECT * FROM fileTrigger WHERE id = :id";
    private static final String getByExtension = "SELECT * FROM fileTrigger WHERE extension = :extension";
    private static final String getCountByExtension = "SELECT count(id) FROM fileTrigger WHERE extension = :extension";
    private static final String deleteById = "DELETE FROM fileTrigger WHERE id = :id";
    private static final String updateById = "UPDATE fileTrigger SET extension = :extension, command = :command, `regexp` = :regexp WHERE id = :id";

    private Integer id;
    private String extension;
    private String command;
    private String regexp;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public FileTriggerModel() {}

    public FileTriggerModel(int id, String extension, String command, String regexp) {
        this.id = id;
        this.extension = extension;
        this.command = command;
        this.regexp = regexp;
    }

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource queryParameters = new MapSqlParameterSource();
            queryParameters.addValue("id", id);
            queryParameters.addValue("extension", extension);
            queryParameters.addValue("command", command);
            queryParameters.addValue("regexp", regexp);
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
            queryParameters.addValue("extension", extension);
            queryParameters.addValue("command", command);
            queryParameters.addValue("regexp", regexp);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, queryParameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return id > 0;
        }
        return false;
    }

    public static FileTriggerModel findByExtension (String extension) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("extension", extension);
        List<Map<String, Object>> rows = template.queryForList(getByExtension, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int templateId = (Integer) result.get("id");
            String extensionTemp = (String) result.get("extension");
            String command = (String) result.get("command");
            String regexp = (String) result.get("regexp");
            return new FileTriggerModel(templateId, extensionTemp, command, regexp);
        }
        return null;
    }

    public boolean validate() {
        // extension
        List<String> extensionErrors = new ArrayList<String>();
        boolean result = true;
        if (extension.length() > 10) {
            result = false;
            extensionErrors.add("Разрешение должно быть меньше 10 символов");
        }
        if (extension.trim().length() == 0) {
            result = false;
            extensionErrors.add("Заполните разрешение");
        } else {
            if (id == null && isExtensionExist(extension)) {
                result = false;
                extensionErrors.add("Такое разрешение уже используется");
            }
        }
        if (extensionErrors.size() > 0) {
            errors.put("extension", extensionErrors);
        }

        // command
        List<String> commandErrors = new ArrayList<String>();
        if (command.trim().length() == 0) {
            result = false;
            commandErrors.add("Введите команду");
        }
        if (commandErrors.size() > 0) {
            errors.put("command", commandErrors);
        }

        // regexp
        List<String> regexpErrors = new ArrayList<String>();
        if (regexp.trim().length() == 0) {
            result = false;
            regexpErrors.add("Введите регулярку");
        } else {
            try {
                Pattern.compile(regexp);
            } catch (PatternSyntaxException exception) {
                result = false;
                regexpErrors.add("Ошибка в регулярке: "+exception.getDescription());
            }
        }
        if (regexpErrors.size() > 0) {
            errors.put("regexp", regexpErrors);
        }


        return result;
    }

    public boolean delete() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    public static FileTriggerModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);

        if (rows.size() > 0) {
            Map<String, Object> result = rows.get(0);
            int templateId = (Integer) result.get("id");
            String extension = (String) result.get("extension");
            String command = (String) result.get("command");
            String regexp = (String) result.get("regexp");
            return new FileTriggerModel(templateId, extension, command, regexp);
        }
        throw new NotFoundException("Триггер не найден", "404");
    }

    public static boolean isExtensionExist(String extension) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("extension", extension);
        return template.queryForObject(getCountByExtension, parameters, Integer.class) > 0;
    }

    public static ArrayList<FileTriggerModel> findAll() throws SQLException {
        ArrayList<FileTriggerModel> result = new ArrayList<FileTriggerModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        rows = template.queryForList(getAll, parameters);
        for (Map<String, Object> row : rows) {
            Integer templateId = (Integer) row.get("id");
            String extension = (String) row.get("extension");
            String command = (String) row.get("command");
            String regexp = (String) row.get("regexp");
            result.add(new FileTriggerModel(templateId, extension, command, regexp));
        }
        return result;
    }
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }
}
