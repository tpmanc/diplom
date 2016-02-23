package models;

import db.Database;
import db.Database2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;

public class FileVersionModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO fileVersion(fileId, version, hash, fileSize) VALUES(:fileId, :version, :hash, :fileSize)";

    private int id;
    private int fileId;
    private String version = "";
    private String hash;
    private long fileSize;

    public boolean update() throws SQLException {
        // TODO file version update
        return false;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("fileId", fileId);
            parameters.addValue("version", version);
            parameters.addValue("hash", hash);
            parameters.addValue("fileSize", fileSize);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return true;
        } else {
            return false;
        }
    }

    public boolean validate() {
        // TODO: file version validate
        return true;
    }

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
