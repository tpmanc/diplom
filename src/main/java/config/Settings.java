package config;

import exceptions.NotFoundException;
import helpers.ConfigDB;
import models.helpers.ActiveDirectorySettings;
import models.helpers.DatabaseSettings;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Настройки
 */
public class Settings {
    private final static String CONFIG_PATH = "/WEB-INF/config/";
    private final static String DATABASE_FILE = "database.properties";
    private final static String ACTIVE_DIRECTORY_FILE = "active-directory.properties";

    private ServletContext servletContext;
    private Properties database;
//    private Properties activeDirectory;

    public final static String DRIVER_FIELD = "db.driverClassName";
    public final static String URL_FIELD = "db.url";
    public final static String USER_FIELD = "db.user";
    public final static String PASSWORD_FIELD = "db.password";
    public final static String POOL_FIELD = "db.poolSize";
    public final static String LDAP_URL = "ldap.url";
    public final static String LDAP_MANAGER_DN = "ldap.manager-dn";
    public final static String LDAP_MANAGER_PASS = "ldap.manager-password";
    public final static String LDAP_USER_SEARCH_FILTER = "ldap.user-search-filter";
    public final static String LDAP_GROUP_SEARCH= "ldap.group-search-base";
    public final static String LDAP_GROUP_SEARCH_FILTER = "ldap.group-search-filter";
    public final static String LDAP_ROLE_ATTRIBUTE = "ldap.role-attribute";

    public Settings() {
        this.servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
        try {
            FileInputStream in = new FileInputStream(servletContext.getRealPath(Settings.getDatabaseFile()));
            database = new Properties();
            database.load(in);
            in.close();

//            in = new FileInputStream(servletContext.getRealPath(Settings.getActiveDirectoryFile()));
//            activeDirectory = new Properties();
//            activeDirectory.load(in);
//            in.close();
        } catch (FileNotFoundException e) {
            // todo 500
            throw new NotFoundException("ERROR");
        } catch (IOException e) {
            // todo 500
            throw new NotFoundException("ERROR");
        }
    }

    private static String getDatabaseFile() {
        return CONFIG_PATH + DATABASE_FILE;
    }
    private static String getActiveDirectoryFile() {
        return CONFIG_PATH + ACTIVE_DIRECTORY_FILE;
    }

    public boolean isDBFilled() {
        boolean isValid = true;
        if (database.getProperty(DRIVER_FIELD).equals("")) {
            isValid = false;
        }
        if (database.getProperty(URL_FIELD).equals("")) {
            isValid = false;
        }
        if (database.getProperty(USER_FIELD).equals("")) {
            isValid = false;
        }
        if (database.getProperty(POOL_FIELD).equals("")) {
            isValid = false;
        }
        if (database.getProperty(POOL_FIELD).equals("")) {
            isValid = false;
        }
        return isValid;
    }

    public boolean isADFilled() {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
        boolean isADValid = true;

        if (System.getProperty(LDAP_URL) == null || System.getProperty(LDAP_URL).equals("")) {
            isADValid = false;
            isFilled.setAdUrl(false);
        }
        if (System.getProperty(LDAP_MANAGER_DN).equals("")) {
            isADValid = false;
            isFilled.setAdManager(false);
        }
        if (System.getProperty(LDAP_MANAGER_PASS).equals("")) {
            isADValid = false;
            isFilled.setAdPassword(false);
        }
        if (System.getProperty(LDAP_USER_SEARCH_FILTER).equals("")) {
            isADValid = false;
            isFilled.setAdUserSearch(false);
        }
        if (System.getProperty(LDAP_GROUP_SEARCH).equals("")) {
            isADValid = false;
            isFilled.setAdGroupSearch(false);
        }
        if (System.getProperty(LDAP_GROUP_SEARCH_FILTER).equals("")) {
            isADValid = false;
            isFilled.setAdGroupFilter(false);
        }
        if (System.getProperty(LDAP_ROLE_ATTRIBUTE).equals("")) {
            isADValid = false;
            isFilled.setAdRoleAttribute(false);
        }

        return isADValid;
    }

    public void setDatabaseFile(String driver, String url, String user, String password) {
        database.setProperty(DRIVER_FIELD, driver);
        database.setProperty(URL_FIELD, url);
        database.setProperty(USER_FIELD, user);
        database.setProperty(PASSWORD_FIELD, password);
    }

    public DatabaseSettings getDatabaseSettings() {
        DatabaseSettings databaseSettings = new DatabaseSettings();
        databaseSettings.setDriver(database.getProperty(DRIVER_FIELD));
        databaseSettings.setUrl(database.getProperty(URL_FIELD));
        databaseSettings.setUser(database.getProperty(USER_FIELD));
        databaseSettings.setPassword(database.getProperty(PASSWORD_FIELD));
        return databaseSettings;
    }

    public void save() {
        try {
            FileOutputStream out = new FileOutputStream(servletContext.getRealPath(Settings.getDatabaseFile()));
            database.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            // todo 500
            throw new NotFoundException("ERROR");
        } catch (IOException e) {
            // todo 500
            throw new NotFoundException("ERROR");
        }
    }
}
