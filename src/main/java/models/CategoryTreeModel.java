package models;

import db.Database;
import exceptions.CustomWebException;
import org.springframework.validation.Errors;
import validators.CategoryTreeValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryTreeModel extends BaseModel implements ModelInterface {
    private static String getById = "SELECT * FROM categoryTree where treeId = ?";
    private static String getAll = "SELECT * FROM categoryTree";
    private static String updateParents = "UPDATE categoryTree SET parent = ? WHERE parent = ?";
    private static String getChildren = "SELECT * FROM categoryTree WHERE parent = ?";
    private static String saveNew = "INSERT INTO categoryTree(parent, categoryId, title, treeId) VALUES (?, ?, ?, ?)";
    private static String deleteById = "DELETE FROM categoryTree WHERE treeId = ?";

    private Errors errors;

    private String parent;
    private String treeId;
    private Integer categoryId = 0;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public CategoryTreeModel(String parent, String treeId, int categoryId, String title) {
        this.parent = parent;
        this.treeId = treeId;
        this.categoryId = categoryId;
        this.title = title;
    }
    public CategoryTreeModel(String parent, String treeId, int categoryId) {
        this.parent = parent;
        this.treeId = treeId;
        this.categoryId = categoryId;
    }
    public CategoryTreeModel(String parent, String treeId, String title) {
        this.parent = parent;
        this.title = title;
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
            String title = res.getString(4);
            return new CategoryTreeModel(parent, treeId, categoryId, title);
        }
        throw new CustomWebException("Запись не найдена");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        return queryAll(getAll);
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
            String title = res.getString(4);
            result.add(new CategoryTreeModel(parent, treeId, categoryId, title));
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
            ps.setString(3, title);
            ps.setString(4, treeId);
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
