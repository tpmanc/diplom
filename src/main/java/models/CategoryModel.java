package models;

import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.validation.Errors;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryModel extends BaseModel implements ModelInterface {
    private static String getById = "SELECT * FROM category where id = :id";
    private static String getAll = "SELECT * FROM category";
    private static String updateElem = "UPDATE category SET parent = :parent, title = :title WHERE id = :id";
    private static String updateParents = "UPDATE category SET parent = :parent WHERE parent = :id";
    private static String getChildren = "SELECT * FROM category WHERE parent = :parentId";
    private static String saveNew = "INSERT INTO category(parent, title, position, isEnabled) VALUES (:parent, :title, :position, :isEnabled)";
    private static String deleteById = "DELETE FROM category WHERE id = :id";
    private static final String getTreeElements = "SELECT * FROM category ORDER BY position ASC, id ASC";
    private static final String getCount = "SELECT count(id) FROM category";

    private Errors errors;

    private int id;
    private int parent;
    private String title;
    private int position = 0;
    private boolean isEnabled = true;

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
        throw new CustomWebException("Категория не найдена");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(getTreeElements);
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", String.valueOf(row.get("id")));
            String parent = String.valueOf(row.get("parent"));
            info.put("parent", parent);
            info.put("title", String.valueOf(row.get("title")));
            result.add(info);
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

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            KeyHolder keyHolder = new GeneratedKeyHolder();
            parameters.addValue("parent", parent);
            parameters.addValue("title", title);
            parameters.addValue("position", position);
            parameters.addValue("isEnabled", isEnabled);
            template.update(saveNew, parameters, keyHolder);
            this.id = keyHolder.getKey().intValue();
            return true;
        }
        return false;
    }

    public boolean validate() {

        return true;
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
