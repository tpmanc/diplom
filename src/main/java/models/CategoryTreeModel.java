package models;

import db.Database;
import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.validation.Errors;
import validators.CategoryTreeValidator;

import java.sql.*;
import java.util.*;

public class CategoryTreeModel extends BaseModel implements ModelInterface {
    private static String getById = "SELECT * FROM categoryTree where treeId = ?";
    private static String getAll = "SELECT * FROM categoryTree";
    private static String updateElem = "UPDATE categoryTree SET parent = :parent, categoryId = :categoryId WHERE treeId = :treeId";
    private static String updateParents = "UPDATE categoryTree SET parent = ? WHERE parent = ?";
    private static String getChildren = "SELECT * FROM categoryTree WHERE parent = ?";
    private static String saveNew = "INSERT INTO categoryTree(parent, categoryId, treeId, sorting) VALUES (?, ?, ?, ?)";
    private static String deleteById = "DELETE FROM categoryTree WHERE treeId = ?";
    private static final String getTreeElements = "SELECT\n"+
            "\tcategoryTree.treeId,\n"+
            "\tcategoryTree.parent,\n"+
            "\tcategoryTree.categoryId,\n"+
            "\tcategoryTree.sorting,\n"+
            "\tcategory.title as \"categoryTitle\"\n"+
            "FROM\n"+
            "\tcategoryTree\n"+
            "LEFT JOIN category ON categoryTree.categoryId = category.id ORDER BY sorting ASC, treeId ASC";

    private Errors errors;

    private String parent;
    private String treeId;
    private Integer categoryId = 0;
    private Integer sorting = 0;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryTreeModel(String parent, String treeId, int categoryId, int sorting) {
        this.parent = parent;
        this.treeId = treeId;
        this.categoryId = categoryId;
        this.sorting = sorting;
    }

    public static CategoryTreeModel findById(String id) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getById);
        ps.setString(1, id);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            String treeId = res.getString(1);
            String parent = res.getString(2);
            int categoryId = res.getInt(3);
            int sorting = res.getInt(4);
            return new CategoryTreeModel(parent, treeId, categoryId, sorting);
        }
        throw new CustomWebException("Запись не найдена");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(getTreeElements);
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("treeId", String.valueOf(row.get("treeId")));
            info.put("categoryId", String.valueOf(row.get("categoryId")));
            String parent = String.valueOf(row.get("parent"));
            info.put("parent", parent);
            info.put("title", String.valueOf(row.get("categoryTitle")));
            result.add(info);
        }
        return result;
    }

    public ArrayList children() throws SQLException {
        ArrayList<CategoryTreeModel> result = new ArrayList<CategoryTreeModel>();
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getChildren);
        ps.setString(1, treeId);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            String treeId = res.getString(1);
            String parent = res.getString(2);
            int categoryId = res.getInt(3);
            int sorting = res.getInt(4);
            result.add(new CategoryTreeModel(parent, treeId, categoryId, sorting));
        }
        return result;
    }

    public boolean update() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("parent", parent);
            parameters.addValue("categoryId", categoryId);
            parameters.addValue("treeId", treeId);
            template.update(updateElem, parameters);
        }
        return false;
    }

    /**
     * Обновление сортировки внутри родителя
     * @param parentId id родителя, внутри которого обвноляем сортировку
     * @param nodeId id элемента, которому задали позицию
     * @param newPosition заданная позиция для элемента с id = nodeId
     * @throws SQLException
     */
    public static void updateSortingOfNode(String parentId, String nodeId, int newPosition) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        String sql = "SELECT * FROM categoryTree WHERE parent = :parentId order by sorting";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("parentId", parentId);
        parameters.addValue("newPosition", newPosition);
        List<Map<String, Object>> rows = template.queryForList(sql, parameters);
        int counter = 0;
        sql = "UPDATE categoryTree SET sorting = :sorting WHERE treeId = :treeId";
        for (Map row : rows) {
            Integer position = (Integer) row.get("sorting");
            if (row.get("treeId").equals(nodeId)) {
                parameters.addValue("treeId", nodeId);
                parameters.addValue("sorting", newPosition);
            } else {
                if (position == newPosition) {
                    counter++;
                }
                parameters.addValue("treeId", row.get("treeId"));
                parameters.addValue("sorting", counter);
            }
            template.update(sql, parameters);
            counter++;
        }
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveNew , Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, parent);
            if (categoryId == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, categoryId);
            }
            ps.setString(3, treeId);
            ps.setInt(4, sorting);
            ps.executeUpdate();
            return true;
        }
        return false;
    }

    public boolean validate() {
        CategoryTreeValidator validator = new CategoryTreeValidator();
        validator.validate(this, errors);
        return true;
//        return !errors.hasErrors();
    }

    public boolean delete() throws SQLException {
        // всем категориям, прявязанным к этому элементу, надо изменить родителя на родителя этого элемента
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(updateParents);
        ps.setString(1, parent);
        ps.setString(2, treeId);
        ps.execute();

        // удаляем этот элемент
        ps = connection.prepareStatement(deleteById);
        ps.setString(1, treeId);
        ps.execute();
        return true;
    }

    public Integer getSorting() {
        return sorting;
    }

    public void setSorting(Integer sorting) {
        this.sorting = sorting;
    }
}
