package config;

import exceptions.InternalException;
import models.SettingsModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class Settings {
    public static final String appDir = "repository";
    public static final String adProperties = "active-directory.properties";
    public static final String dbProperties = "database.properties";
    public static final String logProperties = "log4j.properties";

    private static final String dbDriver = "db.driverClassName";
    private static final String dbUrl = "db.url";
    private static final String dbUser = "db.user";
    private static final String dbPass = "db.password";
    private static final String dbPoolSize = "db.poolSize";

    private static final String ldapUrl = "ldap.url";
    private static final String ldapManagerDN = "ldap.manager-dn";
    private static final String ldapManagerPassword = "ldap.manager-password";
    private static final String ldapUserSearchFilter = "ldap.user-search-filter";
    private static final String ldapGroupSearchBase = "ldap.group-search-base";
    private static final String ldapGroupSearchFilter = "ldap.group-search-filter";
    private static final String ldapRole = "ldap.role-attribute";

    public static final String loggersProperty = "log4j.rootLogger";
    public static final String fileLoggerName = "fileRoll";
    public static final String syslogLoggerName = "SYSLOG";
    private static final String logFilePath = "log4j.appender.fileRoll.File";
    private static final String logFileMaxSize = "log4j.appender.fileRoll.MaxFileSize";
    private static final String logFileCount = "log4j.appender.fileRoll.MaxBackupIndex";
    private static final String logSyslogHost = "log4j.appender.SYSLOG.syslogHost";
    private static final String logSyslogCategory = "log4j.appender.SYSLOG.facility";

    public static String getUploadPath() {
        SettingsModel filePath = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
        if (filePath != null) {
            return filePath.getValue();
        } else {
            throw new InternalException("Путь для сохранения файлов не настроен");
        }
    }

    public static String getRequestUploadPath() {
        SettingsModel filePath = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
        if (filePath != null) {
            return filePath.getValue();
        } else {
            throw new InternalException("Путь для сохранения файлов не настроен");
        }
    }

    public static String getAppHome() {
        String userHome = System.getProperty("user.home");
        String appHome = userHome + File.separator + Settings.appDir;
        File fileDir = new File(appHome);
        // Создаем основную директорию, если ее нет
        if (!fileDir.exists()) {
            boolean res = fileDir.mkdirs();
            if (!res) {
                throw new InternalException("Невозможно создать директорию " + fileDir);
            }
        }
        return appHome;
    }

    public static String getDbPath() {
        String fileDir = getAppHome();
        return fileDir + File.separator + Settings.dbProperties;
    }

    public static String getADPath() {
        String fileDir = getAppHome();
        return fileDir + File.separator + Settings.adProperties;
    }

    public static String getLogPath() {
        String fileDir = getAppHome();
        return fileDir + File.separator + Settings.logProperties;
    }

    public static boolean isAllFilled() {
        boolean isFilled = true;
        // путь для сохранения файлов каталога
        try {
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
        } catch (Exception e) {
            isFilled = false;
        }

        // путь для сохранения файлов заяков
        try {
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
        } catch (Exception e) {
            isFilled = false;
        }

        String dbFilePath = getDbPath();
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                throw new InternalException("Невозможно создать файл " + dbFilePath);
            }
            isFilled = false;
        }

        String adFilePath = getADPath();
        File adFile = new File(adFilePath);
        if (!adFile.exists()) {
            try {
                adFile.createNewFile();
            } catch (IOException e) {
                throw new InternalException("Невозможно создать файл " + adFilePath);
            }
            isFilled = false;
        }

        String logFilePath = getLogPath();
        File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                throw new InternalException("Невозможно создать файл " + logFilePath);
            }
            isFilled = false;
        }

        HashMap<String, String> dbProperties = getDbProperties();
        if (dbProperties.get(dbUrl) == null) {
            isFilled = false;
        }
        if (dbProperties.get(dbUser) == null) {
            isFilled = false;
        }
        if (dbProperties.get(dbPoolSize) == null) {
            isFilled = false;
        }

        HashMap<String, String> adProperties = getADProperties();
        if (adProperties.get(ldapUrl) == null) {
            isFilled = false;
        }
        if (adProperties.get(ldapManagerDN) == null) {
            isFilled = false;
        }
        if (adProperties.get(ldapManagerPassword) == null) {
            isFilled = false;
        }
        if (adProperties.get(ldapUserSearchFilter) == null) {
            isFilled = false;
        }
        if (adProperties.get(ldapGroupSearchBase) == null) {
            isFilled = false;
        }
        if (adProperties.get(ldapGroupSearchFilter) == null) {
            isFilled = false;
        }
        if (adProperties.get(ldapRole) == null) {
            isFilled = false;
        }

        return isFilled;
    }

    public static HashMap<String, String> getDbProperties() {
        String dbFilePath = getDbPath();

        HashMap<String, String> res = new HashMap<>();
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(dbFilePath);
            property.load(fis);
            fis.close();

            String driverName = property.getProperty(dbDriver);
            if (driverName != null) {
                res.put(dbDriver, driverName);
            }

            String url = property.getProperty(dbUrl);
            if (url != null) {
                res.put(dbUrl, url);
            }

            String user = property.getProperty(dbUser);
            if (user != null) {
                res.put(dbUser, user);
            }

            String password = property.getProperty(dbPass);
            if (password != null) {
                res.put(dbPass, password);
            }

            String poolSize = property.getProperty(dbPoolSize);
            if (poolSize != null) {
                res.put(dbPoolSize, poolSize);
            }
            return res;
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getDbPath()+" не найден");
        }
    }

    public static HashMap<String, String> getADProperties() {
        String dbFilePath = getADPath();

        HashMap<String, String> res = new HashMap<>();
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(dbFilePath);
            property.load(fis);
            fis.close();

            String url = property.getProperty(ldapUrl);
            if (url != null) {
                res.put(ldapUrl, url);
            }

            String manager = property.getProperty(ldapManagerDN);
            if (manager != null) {
                res.put(ldapManagerDN, manager);
            }

            String pass = property.getProperty(ldapManagerPassword);
            if (pass != null) {
                res.put(ldapManagerPassword, pass);
            }

            String userSearch = property.getProperty(ldapUserSearchFilter);
            if (userSearch != null) {
                res.put(ldapUserSearchFilter, userSearch);
            }

            String groupSearch = property.getProperty(ldapGroupSearchBase);
            if (groupSearch != null) {
                res.put(ldapGroupSearchBase, groupSearch);
            }

            String groupFilter = property.getProperty(ldapGroupSearchFilter);
            if (groupFilter != null) {
                res.put(ldapGroupSearchFilter, groupFilter);
            }

            String role = property.getProperty(ldapRole);
            if (role != null) {
                res.put(ldapRole, role);
            }
            return res;
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getADPath()+" не найден");
        }
    }

    public static HashMap<String, String> getLogProperties() {
        String lodFilePath = getLogPath();

        HashMap<String, String> res = new HashMap<>();
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(lodFilePath);
            property.load(fis);
            fis.close();

            String loggers = property.getProperty(loggersProperty);
            if (loggers != null) {
                res.put(loggersProperty, loggers);
            }

            String filePath = property.getProperty(logFilePath);
            if (filePath != null) {
                res.put(logFilePath, filePath);
            }

            String fileSize = property.getProperty(logFileMaxSize);
            if (fileSize != null) {
                res.put(logFileMaxSize, fileSize.replace("MB", ""));
            }

            String fileCount = property.getProperty(logFileCount);
            if (fileCount != null) {
                res.put(logFileCount, fileCount);
            }

            String syslogHost = property.getProperty(logSyslogHost);
            if (fileCount != null) {
                res.put(logSyslogHost, syslogHost);
            }

            String syslogCategory = property.getProperty(logSyslogCategory);
            if (syslogCategory != null) {
                res.put(logSyslogCategory, syslogCategory);
            }

            return res;
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getLogPath()+" не найден");
        }
    }

    public static void setDbProperties(String url, String user, String pass, Integer pool) {
        String dbFilePath = getDbPath();

        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(dbFilePath);
            property.load(fis);
            fis.close();

            if (url != null && url.length() > 0) {
                property.setProperty(dbUrl, url);
            }
            if (user != null && user.length() > 0) {
                property.setProperty(dbUser, user);
            }
            if (pass != null && pass.length() > 0) {
                property.setProperty(dbPass, pass);
            }
            if (pool != null && pool > 0) {
                property.setProperty(dbPoolSize, pool.toString());
            }

            FileOutputStream out = new FileOutputStream(dbFilePath);
            property.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.dbProperties+" не найден");
        }
    }

    public static void setAdProperties(String url, String manager, String pass, String userSearch, String groupSearch, String groupFilter, String role) {
        String adFilePath = getADPath();

        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(adFilePath);
            property.load(fis);
            fis.close();

            if (url != null && url.length() > 0) {
                property.setProperty(ldapUrl, url);
            }
            if (manager != null && manager.length() > 0) {
                property.setProperty(ldapManagerDN, manager);
            }
            if (pass != null && pass.length() > 0) {
                property.setProperty(ldapManagerPassword, pass);
            }
            if (userSearch != null && userSearch.length() > 0) {
                property.setProperty(ldapUserSearchFilter, userSearch);
            }
            if (groupSearch != null && groupSearch.length() > 0) {
                property.setProperty(ldapGroupSearchBase, groupSearch);
            }
            if (groupFilter != null && groupFilter.length() > 0) {
                property.setProperty(ldapGroupSearchFilter, groupFilter);
            }
            if (role != null && role.length() > 0) {
                property.setProperty(ldapRole, role);
            }

            FileOutputStream out = new FileOutputStream(adFilePath);
            property.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.adProperties+" не найден");
        }
    }

    public static void setFileProperties(String path, Integer fileSize, Integer fileCount) {
        String logPath = getLogPath();

        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(logPath);
            property.load(fis);
            fis.close();

            String loggers = property.getProperty(loggersProperty);
            if (loggers == null) {
                property.setProperty(loggersProperty, "info");
                loggers = "info";
            }
            if (!loggers.contains(fileLoggerName)) {
                property.setProperty(loggersProperty, loggers + ", " + fileLoggerName);
            }
            property.setProperty("log4j.appender.fileRoll", "org.apache.log4j.RollingFileAppender");

            if (path != null && path.length() > 0) {
                property.setProperty(logFilePath, path);
            }
            if (fileSize != null && fileSize > 0) {
                property.setProperty(logFileMaxSize, fileSize.toString() + "MB");
            }
            if (fileCount != null && fileCount > 0) {
                property.setProperty(logFileCount, fileCount.toString());
            }

            property.setProperty("log4j.appender.fileRoll.layout", "org.apache.log4j.PatternLayout");
            property.setProperty("log4j.appender.fileRoll.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");

            FileOutputStream out = new FileOutputStream(logPath);
            property.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getLogPath()+" не найден");
        }
    }

    public static void setSyslogProperties(String host, String category) {
        String logPath = getLogPath();

        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(logPath);
            property.load(fis);
            fis.close();

            String loggers = property.getProperty(loggersProperty);
            if (loggers == null) {
                property.setProperty(loggersProperty, "info");
                loggers = "info";
            }
            if (!loggers.contains(syslogLoggerName)) {
                property.setProperty(loggersProperty, loggers + ", " + syslogLoggerName);
            }
            property.setProperty("log4j.appender.SYSLOG", "org.apache.log4j.net.SyslogAppender");

            if (host != null && host.length() > 0) {
                property.setProperty(logSyslogHost, host);
            }
            if (category != null && category.length() > 0) {
                property.setProperty(logSyslogCategory, category);
            }

            property.setProperty("log4j.appender.SYSLOG.layout", "org.apache.log4j.PatternLayout");
            property.setProperty("log4j.appender.SYSLOG.layout.conversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");

            FileOutputStream out = new FileOutputStream(logPath);
            property.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getLogPath()+" не найден");
        }
    }

    public static void disableFileLogger() {
        String logPath = getLogPath();

        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(logPath);
            property.load(fis);
            fis.close();

            String loggers = property.getProperty(loggersProperty);
            if (loggers == null) {
                property.setProperty(loggersProperty, "info");
                loggers = "info";
            }
            if (loggers.contains(fileLoggerName)) {
                loggers = loggers.replace(", " + fileLoggerName, "");
                property.setProperty(loggersProperty, loggers);
            }

            FileOutputStream out = new FileOutputStream(logPath);
            property.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getLogPath()+" не найден");
        }
    }

    public static void disableSyslogLogger() {
        String logPath = getLogPath();

        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream(logPath);
            property.load(fis);
            fis.close();

            String loggers = property.getProperty(loggersProperty);
            if (loggers == null) {
                property.setProperty(loggersProperty, "info");
                loggers = "info";
            }
            if (loggers.contains(syslogLoggerName)) {
                loggers = loggers.replace(", " + syslogLoggerName, "");
                property.setProperty(loggersProperty, loggers);
            }

            FileOutputStream out = new FileOutputStream(logPath);
            property.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new InternalException("Файл "+Settings.getLogPath()+" не найден");
        }
    }
}
