package models;

import db.Database;
import db.Database2;
import exceptions.CustomWebException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.Errors;
import validators.CategoryTreeValidator;

import java.sql.*;
import java.util.*;

public class CategoryTreeModel extends BaseModel implements ModelInterface {
    private static String getById = "SELECT * FROM categoryTree where treeId = ?";
    private static String getAll = "SELECT * FROM categoryTree";
    private static String updateParents = "UPDATE categoryTree SET parent = ? WHERE parent = ?";
    private static String getChildren = "SELECT * FROM categoryTree WHERE parent = ?";
    private static String saveNew = "INSERT INTO categoryTree(parent, categoryId, treeId) VALUES (?, ?, ?)";
    private static String deleteById = "DELETE FROM categoryTree WHERE treeId = ?";
    private static final String getTreeElements = "SELECT\n"+
            "\tcategoryTree.treeId,\n"+
            "\tcategoryTree.parent,\n"+
            "\tcategoryTree.categoryId,\n"+
            "\tcategory.title as \"categoryTitle\"\n"+
            "FROM\n"+
            "\tcategoryTree\n"+
            "LEFT JOIN category ON categoryTree.categoryId = category.id";

    private Errors errors;

    private String parent;
    private String treeId;
    private Integer categoryId = 0;

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

    public CategoryTreeModel(String parent, String treeId, int categoryId) {
        this.parent = parent;
        this.treeId = treeId;
        this.categoryId = categoryId;
    }
    public CategoryTreeModel(String parent, String treeId) {
        this.parent = parent;
        this.treeId = treeId;
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
            return new CategoryTreeModel(parent, treeId, categoryId);
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
            result.add(new CategoryTreeModel(parent, treeId, categoryId));
        }
        return result;
    }

    public boolean update() throws SQLException {
        return false;
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
}
