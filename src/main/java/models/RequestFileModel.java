package models;

import config.Settings;
import db.Database2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestFileModel implements ModelInterface {
    private static final String saveNew = "INSERT INTO requestFile(requestId, hash, fileName, fileSize, extension) VALUES(:requestId, :hash, :fileName, :fileSize, :extension)";
    private static final String isFileExist = "SELECT count(id) FROM requestFile WHERE hash = :hash AND fileSize = :fileSize";
    private static final String deleteById = "DELETE FROM requestFile WHERE id = :id";

    private int id;
    private int requestId;
    private String hash;
    private String fileName;
    private String extension;
    private long fileSize;

    public RequestFileModel() {}
    public RequestFileModel(int id, int requestId, String hash, String fileName, String extension, long fileSize) {
        this.id = id;
        this.requestId = requestId;
        this.hash = hash;
        this.fileName = fileName;
        this.extension = extension;
        this.fileSize = fileSize;
    }

    public boolean update() throws SQLException {
        return false;
    }

    public boolean add() throws SQLException {
        if (validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("requestId", requestId);
            parameters.addValue("hash", hash);
            parameters.addValue("fileName", fileName);
            parameters.addValue("fileSize", fileSize);
            parameters.addValue("extension", extension);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = template.update(saveNew, parameters, keyHolder);
            if (rows > 0) {
                this.id = keyHolder.getKey().intValue();
                return true;
            }
        }
        return false;
    }

    public boolean validate() {
        // todo
        return true;
    }

    public boolean delete() throws SQLException {
        String uploadPath = Settings.getRequestUploadPath();
        String firstDir = hash.substring(0, 2);
        String secondDir = hash.substring(2, 4);
        StringBuilder newFileName = new StringBuilder();
        newFileName
                .append(uploadPath)
                .append(File.separator)
                .append(firstDir)
                .append(File.separator)
                .append(secondDir)
                .append(File.separator)
                .append(fileName);
        File file = new File(newFileName.toString());
        boolean isDeleted = file.delete();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
    }

    /**
     * Проверка на дублирование файла
     * Проверка осуществляется по хэшу и размеру файла
     * @return True если такой файл уже есть, иначе false
     */
    public static boolean isExist(String hash, long fileSize) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("hash", hash);
        parameters.addValue("fileSize", fileSize);
        Integer count = template.queryForObject(isFileExist, parameters, Integer.class);
        return count > 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
