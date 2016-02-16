package models;

import db.Database;

import java.sql.*;

public class FileVersionModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO fileVersion(fileId, version, hash, fileSize) VALUES(?, ?, ?, ?)";

    private int id;
    private int fileId;
    private String version = "";
    private String hash;
    private long fileSize;

    @Override
    public boolean update() throws SQLException {
        // TODO file version update
        return false;
    }

    @Override
    public boolean add() throws SQLException {
        if (this.validate()) {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(saveNew, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, fileId);
            ps.setString(2, version);
            ps.setString(3, hash);
            ps.setLong(4, fileSize);
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

    @Override
    public boolean validate() {
        // TODO: file version validate
        return true;
    }

    @Override
    public boolean delete() throws SQLException {
        // TODO: file version delete
        return false;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
