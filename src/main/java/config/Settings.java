package config;

import exceptions.InternalException;
import models.LogModel;
import models.SettingsModel;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class Settings {
    public static final String appDir = "repository";
    public static final String adProperties = "active-directory.properties";
    public static final String dbProperties = "database.properties";

    public static String getUploadPath(HttpServletRequest request) {
        String uploadPath = request.getServletContext().getRealPath("upload");
        File uploadRootDir = new File(uploadPath);
        // Создаем основную директорию, если ее нет
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        return uploadPath;
    }

    public static boolean isAllFilled() {
        boolean isFilled = true;
        // путь для сохранения файлов каталога
        SettingsModel catalogFilePath = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
        if (catalogFilePath == null) {
            catalogFilePath = new SettingsModel(SettingsModel.UPLOAD_PATH);
            try {
                catalogFilePath.add();
                isFilled = false;
            } catch (SQLException e) {
                throw new InternalException("Ошибка при добавлении настройки в БД");
            }
        }
        if (catalogFilePath.getValue().equals("")) {
            isFilled = false;
        }

        // путь для сохранения файлов заяков
        SettingsModel requestFilePath = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
        if (requestFilePath == null) {
            requestFilePath = new SettingsModel(SettingsModel.UPLOAD_REQUEST_PATH);
            try {
                requestFilePath.add();
                isFilled = false;
            } catch (SQLException e) {
                throw new InternalException("Ошибка при добавлении настройки в БД");
            }
        }
        if (requestFilePath.getValue().equals("")) {
            isFilled = false;
        }

        String userHome = System.getProperty("user.home");
        File fileDir = new File(userHome + File.separator + Settings.appDir);
        // Создаем основную директорию, если ее нет
        if (!fileDir.exists()) {
            boolean res = fileDir.mkdirs();
            if (!res) {
                throw new InternalException("Невозможно создать директорию " + fileDir);
            }
        }
        String dbFilePath = fileDir + File.separator + Settings.dbProperties;
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                throw new InternalException("Невозможно создать файл " + dbFilePath);
            }
            isFilled = false;
        }

        String adFilePath = fileDir + File.separator + Settings.adProperties;
        File adFile = new File(adFilePath);
        if (!dbFile.exists()) {
            try {
                adFile.createNewFile();
            } catch (IOException e) {
                throw new InternalException("Невозможно создать файл " + adFilePath);
            }
            isFilled = false;
        }

        return isFilled;
    }
}
