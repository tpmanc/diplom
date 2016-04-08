package models;

import db.Database2;
import exceptions.NotFoundException;
import models.helpers.CategoryFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class CategoryModel implements ModelInterface {
    private static String getById = "SELECT * FROM category where id = :id";
    private static String getAll = "SELECT * FROM category";
    private static String updateElem = "UPDATE category SET parent = :parent, title = :title WHERE id = :id";
    private static String updateParents = "UPDATE category SET parent = :parent WHERE parent = :id";
    private static String getChildren = "SELECT * FROM category WHERE parent = :parentId";
    private static String saveNew = "INSERT INTO category(parent, title, position) VALUES (:parent, :title, :position)";
    private static String deleteById = "DELETE FROM category WHERE id = :id";
    private static final String getTreeElements = "SELECT * FROM category ORDER BY position ASC, id ASC";
    private static final String getCount = "SELECT count(id) FROM category";
    private static final String getFiles = "SELECT fileId FROM fileCategory WHERE categoryId = :categoryId;";
    private static final String getFilesInfo = "SELECT file.id, file.title, fileVersion.version, fileVersion.date, fileVersion.id as versionId, user.displayName FROM file " +
            " LEFT JOIN fileVersion ON fileVersion.id = (SELECT id FROM fileVersion WHERE fileVersion.fileId = file.id AND fileVersion.isDisabled = :isDisabled ORDER BY version DESC LIMIT 1) " +
            " LEFT JOIN user ON user.id = fileVersion.userId WHERE file.id IN (:idList) ORDER BY fileVersion.version DESC LIMIT :limit OFFSET :offset";

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    private int id;
    private int parent;
    private String title;
    private int position = 0;

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CategoryModel(int parent, int position, String title) {
        this.parent = parent;
        this.position = position;
        this.title = title;
    }

    public CategoryModel(int id, int parent, int position, String title) {
        this.parent = parent;
        this.id = id;
        this.position = position;
        this.title = title;
    }

    public static CategoryModel findById(int id) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String,Object>> res = template.queryForList(getById, parameters);
        if (!res.isEmpty()) {
            int categoryId = Integer.parseInt(String.valueOf(res.get(0).get("id")));
            int parent = Integer.parseInt(String.valueOf(res.get(0).get("parent")));
            String title = String.valueOf(res.get(0).get("title"));
            int position = Integer.parseInt(String.valueOf(res.get(0).get("position")));
            return new CategoryModel(categoryId, parent, position, title);
        }
        throw new NotFoundException("Категория не найдена");
    }

    public static ArrayList<CategoryModel> findAll() throws SQLException {
        ArrayList<CategoryModel> result = new ArrayList<CategoryModel>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(getTreeElements);
        for (Map row : rows) {
            Integer modelId =  (Integer) row.get("id");
            Integer parent =  (Integer) row.get("parent");
            String title = (String) row.get("title");
            Integer position = (Integer) row.get("position");
            result.add(new CategoryModel(modelId, parent, position, title));
        }
        return result;
    }

    public ArrayList children() throws SQLException {
        ArrayList<CategoryModel> result = new ArrayList<CategoryModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("parentId", id);
        List<Map<String, Object>> rows = template.queryForList(getChildren, parameters);

        for (Map row : rows) {
            Integer categoryId = (Integer) row.get("id");
            Integer parent = (Integer) row.get("parent");
            String title = (String) row.get("title");
            Integer position = (Integer) row.get("position");
            result.add(new CategoryModel(categoryId, parent, position, title));
        }
        return result;
    }

    /**
     * Изменение информации о категории
     * @return Boolean
     * @throws SQLException
     */
    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            parameters.addValue("parent", parent);
            parameters.addValue("title", title);
            template.update(updateElem, parameters);
        }
        return false;
    }

    /**
     * Получение количества категорий
     * @return Количество категорий
     */
    public static int getCount() {
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        return template.queryForObject(getCount, Integer.class);
    }

    /**
     * Обновление сортировки внутри родителя
     * @param parentId id родителя, внутри которого обвноляем сортировку
     * @param nodeId id элемента, которому задали позицию
     * @param newPosition заданная позиция для элемента с id = nodeId
     * @throws SQLException
     */
    public static void updateSortingOfNode(int parentId, int nodeId, int newPosition) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        String sql = "SELECT * FROM category WHERE parent = :parentId ORDER BY position";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("parentId", parentId);
        List<Map<String, Object>> rows = template.queryForList(sql, parameters);
        int counter = 0;
        sql = "UPDATE category SET position = :position WHERE id = :id";
        for (Map row : rows) {
            Integer position = (Integer) row.get("position");
            Integer id = (Integer) row.get("id");
            if (id == nodeId) {
                parameters.addValue("id", nodeId);
                parameters.addValue("position", newPosition);
            } else {
                if (position == newPosition) {
                    counter++;
                }
                parameters.addValue("id", id);
                parameters.addValue("position", counter);
                counter++;
            }
            template.update(sql, parameters);
        }
    }

    /**
     * Список файлов в категории постранично
     */
    public ArrayList<CategoryFile> getFiles(int limit, int offset) {
        ArrayList<CategoryFile> result = new ArrayList<CategoryFile>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("categoryId", id);
        List<Map<String, Object>> idArray = template.queryForList(getFiles, parameters);
        List<Integer> ids = new ArrayList<Integer>();
        for (Map row : idArray) {
            Integer fileId = (Integer) row.get("fileId");
            ids.add(fileId);
        }

        if (ids.size() > 0) {
            parameters.addValue("limit", limit);
            parameters.addValue("offset", offset);
            parameters.addValue("idList", ids);
            parameters.addValue("isDisabled", false);
            List<Map<String, Object>> rows = template.queryForList(getFilesInfo, parameters);
            for (Map row : rows) {
                Integer fileId = (Integer) row.get("id");
                Integer versionId = (Integer) row.get("versionId");
                String title = (String) row.get("title");
                String version = (String) row.get("version");
                Long intDate = (Long) row.get("date");
                java.util.Date date = new java.util.Date(intDate);
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                String strDate = df.format(date);
                String userDN = (String) row.get("displayName");

                CategoryFile categoryFile = new CategoryFile();
                categoryFile.setId(fileId);
                categoryFile.setVersionId(versionId);
                categoryFile.setTitle(title);
                categoryFile.setVersion(version);
                categoryFile.setDate(strDate);
                categoryFile.setUserDN(userDN);
                result.add(categoryFile);
            }
        }

        return result;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            KeyHolder keyHolder = new GeneratedKeyHolder();
            parameters.addValue("parent", parent);
            parameters.addValue("title", title);
            parameters.addValue("position", position);
            template.update(saveNew, parameters, keyHolder);
            this.id = keyHolder.getKey().intValue();
            return true;
        }
        return false;
    }

    public boolean validate() {
        // title
        List<String> titleErrors = new ArrayList<String>();
        boolean isValid = true;
        if (title.length() > 255) {
            isValid = false;
            titleErrors.add("Название должно быть меньше 255 символов");
        }
        if (title.trim().length() == 0) {
            isValid = false;
            titleErrors.add("Заполните название");
        }
        if (titleErrors.size() > 0) {
            errors.put("title", titleErrors);
        }

        // parent
        List<String> parentErrors = new ArrayList<String>();
        if (parent < 0) {
            isValid = false;
            parentErrors.add("Id родителя должен быть >= 0");
        }
        if (parentErrors.size() > 0) {
            errors.put("parent", parentErrors);
        }

        // position
        List<String> positionErrors = new ArrayList<String>();
        if (parent < 0) {
            isValid = false;
            positionErrors.add("Позиция должена быть >= 0");
        }
        if (parentErrors.size() > 0) {
            errors.put("position", positionErrors);
        }

        return isValid;
    }

    public boolean delete() throws SQLException {
        // всем категориям, прявязанным к этому элементу, надо изменить родителя на родителя этого элемента
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("parent", parent);
        parameters.addValue("id", id);
        template.update(updateParents, parameters);

        // удаляем этот элемент
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }
}
