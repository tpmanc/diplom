package models;

import db.Database2;
import exceptions.NotFoundException;
import models.helpers.CategoryFile;
import models.helpers.FileCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sun.plugin.cache.FileVersion;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileModel implements ModelInterface {
    private static final String getById = "SELECT * FROM file WHERE id = :id";
    private static final String getByTitle = "SELECT * FROM file WHERE title = :title";
    private static final String saveNew = "INSERT INTO file(title) VALUES(:title)";
    private static final String getAll = "SELECT * FROM file";
    private static final String getAllLimit = "SELECT * FROM file LIMIT :limit OFFSET :offset";
    private static final String getCount = "SELECT count(id) FROM file";
    private static final String getVersionCountExclude = "SELECT count(id) FROM fileVersion WHERE fileId = :fileId AND id <> :id";
    private static final String getTitles = "SELECT id, title FROM file WHERE title LIKE :str";
    private static final String getVersions = "SELECT * FROM fileVersion WHERE fileId = :fileId ORDER BY CONVERT(version, decimal) DESC";
    private static final String getEnabledVersions = "SELECT * FROM fileVersion WHERE fileId = :fileId AND isDisabled = :isDisabled ORDER BY CONVERT(version, decimal) DESC";
    private static final String deleteById = "DELETE FROM file WHERE id = :id";
    private static final String getAllUnfilled = "SELECT id,fileName as title, 0 as isNoCategory FROM fileVersion WHERE isFilled = :isFilled " +
                                                " UNION " +
                                                " SELECT file.id,file.title as title, 1 as isNoCategory FROM file LEFT JOIN fileCategory ON fileCategory.fileId = file.id WHERE fileCategory.id IS NULL GROUP BY file.id " +
                                                " LIMIT :limit OFFSET :offset;";
    private static final String getAllUnfilledCount = "SELECT count(id) AS count FROM fileVersion WHERE isFilled = :isFilled" +
            " UNION " +
            " SELECT count(file.id) AS count FROM file LEFT JOIN fileCategory ON fileCategory.fileId = file.id WHERE fileCategory.id IS NULL GROUP BY file.id";
    private static final String getFilesByTitle = "SELECT file.id, file.title, fileVersion.version, fileVersion.date, user.displayName FROM file " +
            "LEFT JOIN fileVersion ON fileVersion.id = (SELECT id FROM fileVersion WHERE fileVersion.fileId = file.id ORDER BY version DESC LIMIT 1) " +
            "LEFT JOIN user ON user.id = fileVersion.userId WHERE file.title like :str ORDER BY fileVersion.version DESC LIMIT 100";

    private int id;
    private String title;
    public HashMap<String, List<String>> errors = new HashMap<String, List<String>>();

    public static final int PAGE_COUNT = 10;

    public boolean update() throws SQLException {
        // nothing
        return false;
    }

    public FileModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public FileModel() {
    }

    public static FileModel findById(int id) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Map<String, Object>> rows = template.queryForList(getById, parameters);
        for (Map row : rows) {
            Integer fileId = (Integer) row.get("id");
            String title = (String) row.get("title");
            return new FileModel(fileId, title);
        }
        throw new NotFoundException("Файл не найден");
    }

    public static FileModel findByTitle(String title) throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", title);
        List<Map<String, Object>> rows = template.queryForList(getByTitle, parameters);
        for (Map row : rows) {
            Integer fileId = (Integer) row.get("id");
            String fileTitle = (String) row.get("title");
            return new FileModel(fileId, fileTitle);
        }
        return null;
    }

    public static ArrayList<FileModel> findAll() throws SQLException {
        ArrayList<FileModel> result = new ArrayList<FileModel>();
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        List<Map<String, Object>> rows = template.queryForList(getAll);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            result.add(new FileModel(modelId, title));
        }
        return result;
    }

    public static ArrayList<FileModel> findAll(int limit, int offset) throws SQLException {
        ArrayList<FileModel> result = new ArrayList<FileModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAllLimit, parameters);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            result.add(new FileModel(modelId, title));
        }
        return result;
    }

    public static int getCount() {
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        return template.queryForObject(getCount, Integer.class);
    }

    public boolean add() throws SQLException {
        if (this.validate()) {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("title", title);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(saveNew, parameters, keyHolder);
            id = keyHolder.getKey().intValue();
            return id > 0;
        } else {
            return false;
        }
    }

    public static ArrayList<FileModel> findTitles(String query){
        ArrayList<FileModel> result = new ArrayList<FileModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("str", "%" + query + "%");
        List<Map<String, Object>> rows = template.queryForList(getTitles, parameters);
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            String title = (String) row.get("title");
            result.add(new FileModel(modelId, title));
        }
        return result;
    }

    public static ArrayList<CategoryFile> findFilesByTitles(String query){
        ArrayList<CategoryFile> result = new ArrayList<CategoryFile>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("str", "%" + query + "%");
        List<Map<String, Object>> rows = template.queryForList(getFilesByTitle, parameters);
        for (Map row : rows) {
            Integer fileId = (Integer) row.get("id");
            String title = (String) row.get("title");
            String version = (String) row.get("version");
            Long intDate = (Long) row.get("date");
            java.util.Date date = new java.util.Date(intDate);
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String strDate = df.format(date);
            String userDN = (String) row.get("displayName");

            CategoryFile file = new CategoryFile();
            file.setId(fileId);
            file.setTitle(title);
            file.setVersion(version);
            file.setDate(strDate);
            file.setUserDN(userDN);
            result.add(file);
        }
        return result;
    }

    public static ArrayList<HashMap> findUnfilled(int limit, int offset) {
        ArrayList<HashMap> result = new ArrayList<HashMap>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("isFilled", false);
        parameters.addValue("limit", limit);
        parameters.addValue("offset", offset);
        List<Map<String, Object>> rows = template.queryForList(getAllUnfilled, parameters);
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("id", String.valueOf(row.get("id")));
            info.put("title", String.valueOf(row.get("title")));
            info.put("isNoCategory", String.valueOf(row.get("isNoCategory")));
            result.add(info);
        }
        return result;
    }

    public static int getUnfilledCount() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("isFilled", false);
        List<Map<String, Object>> rows = template.queryForList(getAllUnfilledCount, parameters);
        int count = 0;
        for (Map row : rows) {
            HashMap<String, String> info = new HashMap<String, String>();
            long c = (Long) row.get("count");
            count += c;
        }
        return count;
    }

    public FileVersionModel getLastVersion() {
        String sql = "SELECT * FROM fileVersion WHERE fileId = :fileId ORDER BY CONVERT(version, decimal) DESC LIMIT 1;";
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", id);
        List<Map<String, Object>> rows = template.queryForList(sql, parameters);
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            Integer fileId = (Integer) row.get("fileId");
            Integer userId = (Integer) row.get("userId");
            String version = (String) row.get("version");
            String fileName = (String) row.get("fileName");
            String hash = (String) row.get("hash");
            Long fileSize = (Long) row.get("fileSize");
            Long date = (Long) row.get("date");
            Boolean isFilled = ((Integer) row.get("isFilled") == 1);
            Boolean isDisabled = ((Integer) row.get("isDisabled") == 1);
            return  new FileVersionModel(id, fileId, userId, version, hash, fileSize, date, isFilled, fileName, isDisabled);
        }
        throw new NotFoundException("Версия не найдена");
    }

    public ArrayList<FileVersionModel> getVersionList(boolean onlyEnabled) {
        ArrayList<FileVersionModel> result = new ArrayList<FileVersionModel>();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", id);
        parameters.addValue("isDisabled", false);
        List<Map<String, Object>> rows;
        if (onlyEnabled) {
            rows = template.queryForList(getEnabledVersions, parameters);
        } else {
            rows = template.queryForList(getVersions, parameters);
        }
        for (Map row : rows) {
            Integer modelId = (Integer) row.get("id");
            Integer fileId = (Integer) row.get("fileId");
            Integer userId = (Integer) row.get("userId");
            String version = (String) row.get("version");
            String hash = (String) row.get("hash");
            String fileName = (String) row.get("fileName");
            Long fileSize = (Long) row.get("fileSize");
            Long date = (Long) row.get("date");
            Boolean isFilled = (Integer) row.get("isFilled") == 1;
            Boolean isDisabled = (Integer) row.get("isDisabled") == 1;
            result.add(new FileVersionModel(modelId, fileId, userId, version, hash, fileSize, date, isFilled, fileName, isDisabled));
        }
        return result;
    }

    public int getVersionCount(int excludeVersionId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("fileId", id);
        parameters.addValue("id", excludeVersionId);
        return template.queryForObject(getVersionCountExclude, parameters, Integer.class);
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

        return isValid;
    }

    public boolean delete() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(Database2.getInstance().getBds());
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        // удаляем свойства файла
        FilePropertyModel.deleteByVersion(id);

        // удаляем связь с категориями
        FileCategoryModel.deleteByFile(id);

        // удаляем сам файл
        parameters.addValue("id", id);
        int rows = template.update(deleteById, parameters);
        return rows > 0;
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
