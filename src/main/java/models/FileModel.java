package models;

import db.Database;
import exceptions.CustomWebException;
import org.springframework.security.acls.model.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileModel extends BaseModel implements ModelInterface {
    private static final String getById = "SELECT * FROM file WHERE id = ?";
//    private static final String saveById = "UPDATE file SET title = ?, chpu = ?, isEnabled = ? WHERE id = ?";
    private static final String saveNew = "INSERT INTO file(title) VALUES(?)";
    private static final String getAll = "SELECT * FROM file";
    private static final String isFileExist = "SELECT * FROM fileVersion WHERE hash = ? AND fileSize = ?";
//    private static final String deleteById = "DELETE FROM file WHERE id = ?";

    private int id;
    private String title;
    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public boolean update() throws SQLException {
        // TODO: update data
        return false;
    }

    public FileModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public FileModel() {
    }

    public static FileModel findById(int id) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement ps = connection.prepareStatement(getById);
        ps.setInt(1, id);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            int fileId = res.getInt(1);
            String title = res.getString(2);
            return new FileModel(fileId, title);
        }
        throw new NotFoundException("File not found");
    }

    public static ArrayList<HashMap> findAll() throws SQLException {
        return queryAll(getAll);
    }

    /**
     * Проверка на дублирование файла
     * Проверка осуществляется по хэшу и размеру файла
     * @return True если такой файл уже есть, иначе false
     */
    public static boolean isExist(String hash, long fileSize) {
        Connection connection = Database.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(isFileExist);
            ps.setString(1, hash);
            ps.setLong(2, fileSize);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new CustomWebException("Ошибка при обращении в БД");
        }
        return false;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveNew, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            int affectedRows = ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean validate() {
        return true;
    }

    public boolean delete() throws SQLException {
        return false;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
