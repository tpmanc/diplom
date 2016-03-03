package models;

import db.Database2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileVersionModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO fileVersion(fileId, version, hash, fileSize, date, isFilled) VALUES(:fileId, :version, :hash, :fileSize, :date, :isFilled)";

    private int id;
    private int fileId;
    private String version = "";
    private String hash;
    private long fileSize;
    private long date;
    private boolean isFilled;

    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public boolean update() throws SQLException {
        // TODO file version update
        return false;
    }

    public FileVersionModel() {

    }

    public FileVersionModel(int id, int fileId, String version, String hash, long fileSize, long date, boolean isFilled) {
        this.id = id;
        this.fileId = fileId;
        this.version = version;
        this.hash = hash;
        this.fileSize = fileSize;
        this.date = date;
        this.isFilled = isFilled;
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("fileId", fileId);
            parameters.addValue("version", version);
            parameters.addValue("hash", hash);
            parameters.addValue("fileSize", fileSize);
            parameters.addValue("date", date);
            parameters.addValue("isFilled", isFilled);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return true;
        } else {
            return false;
        }
    }

    public boolean validate() {
        // hash
        List<String> hashErrors = new ArrayList<String>();
        boolean isValid = true;
        if (hash.length() > 255) {
            isValid = false;
            hashErrors.add("Хэш должен быть меньше 255 символов");
        }
        if (hash.trim().length() == 0) {
            isValid = false;
            hashErrors.add("Заполните хэш");
        }
        if (hashErrors.size() > 0) {
            errors.put("hash", hashErrors);
        }

        // version

        List<String> versionErrors = new ArrayList<String>();
        if (version.length() > 255) {
            isValid = false;
            versionErrors.add("Версия должна быть меньше 255 символов");
        }
        if (versionErrors.size() > 0) {
            errors.put("version", versionErrors);
        }

        // file id
        List<String> fileIdErrors = new ArrayList<String>();
        if (fileId < 0) {
            isValid = false;
            fileIdErrors.add("Id файла должен быть >= 0");
        }
        if (fileIdErrors.size() > 0) {
            errors.put("fileId", fileIdErrors);
        }

        // file size
        List<String> fileSizeErrors = new ArrayList<String>();
        if (fileSize < 0) {
            isValid = false;
            fileSizeErrors.add("Размер должен быть >= 0");
        }
        if (fileSizeErrors.size() > 0) {
            errors.put("fileSize", fileSizeErrors);
        }

        return isValid;
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

    public String getVersion() {
        return version;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }
}
