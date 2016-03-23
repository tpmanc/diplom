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
    private Properties activeDirectory;

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

            in = new FileInputStream(servletContext.getRealPath(Settings.getActiveDirectoryFile()));
            activeDirectory = new Properties();
            activeDirectory.load(in);
            in.close();
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

    public boolean isFilled() {
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

        if (activeDirectory.getProperty(LDAP_URL).equals("")) {
            isValid = false;
        }
        if (activeDirectory.getProperty(LDAP_MANAGER_DN).equals("")) {
            isValid = false;
        }
        if (activeDirectory.getProperty(LDAP_MANAGER_PASS).equals("")) {
            isValid = false;
        }
        if (activeDirectory.getProperty(LDAP_USER_SEARCH_FILTER).equals("")) {
            isValid = false;
        }
        if (activeDirectory.getProperty(LDAP_GROUP_SEARCH).equals("")) {
            isValid = false;
        }
        if (activeDirectory.getProperty(LDAP_GROUP_SEARCH_FILTER).equals("")) {
            isValid = false;
        }
        if (activeDirectory.getProperty(LDAP_ROLE_ATTRIBUTE).equals("")) {
            isValid = false;
        }
        return isValid;
    }

    public void setDatabaseFile(String driver, String url, String user, String password) {
        database.setProperty(DRIVER_FIELD, driver);
        database.setProperty(URL_FIELD, url);
        database.setProperty(USER_FIELD, user);
        database.setProperty(PASSWORD_FIELD, password);
    }

    public void setActiveDirectoryFile(String url, String manager, String password, String userSearch, String groupSearch, String groupFilter, String roleAttribute) {
        activeDirectory.setProperty(LDAP_URL, url);
        activeDirectory.setProperty(LDAP_MANAGER_DN, manager);
        activeDirectory.setProperty(LDAP_MANAGER_PASS, password);
        activeDirectory.setProperty(LDAP_USER_SEARCH_FILTER, userSearch);
        activeDirectory.setProperty(LDAP_GROUP_SEARCH, groupSearch);
        activeDirectory.setProperty(LDAP_GROUP_SEARCH_FILTER, groupFilter);
        activeDirectory.setProperty(LDAP_ROLE_ATTRIBUTE, roleAttribute);
    }

    public DatabaseSettings getDatabaseSettings() {
        DatabaseSettings databaseSettings = new DatabaseSettings();
        databaseSettings.setDriver(database.getProperty(DRIVER_FIELD));
        databaseSettings.setUrl(database.getProperty(URL_FIELD));
        databaseSettings.setUser(database.getProperty(USER_FIELD));
        databaseSettings.setPassword(database.getProperty(PASSWORD_FIELD));
        return databaseSettings;
    }

    public ActiveDirectorySettings getActiveDirectorySettings() {
        ActiveDirectorySettings activeDirectorySettings = new ActiveDirectorySettings();
        activeDirectorySettings.setUrl(activeDirectory.getProperty(LDAP_URL));
        activeDirectorySettings.setManager(activeDirectory.getProperty(LDAP_MANAGER_DN));
        activeDirectorySettings.setPassword(activeDirectory.getProperty(LDAP_MANAGER_PASS));
        activeDirectorySettings.setUserFilter(activeDirectory.getProperty(LDAP_USER_SEARCH_FILTER));
        activeDirectorySettings.setGroupSearch(activeDirectory.getProperty(LDAP_GROUP_SEARCH));
        activeDirectorySettings.setGroupFilter(activeDirectory.getProperty(LDAP_GROUP_SEARCH_FILTER));
        activeDirectorySettings.setRoleAttribute(activeDirectory.getProperty(LDAP_ROLE_ATTRIBUTE));
        return activeDirectorySettings;
    }

    public void save() {
        try {
            FileOutputStream out = new FileOutputStream(servletContext.getRealPath(Settings.getDatabaseFile()));
            database.store(out, null);
            out.close();

            out = new FileOutputStream(servletContext.getRealPath(Settings.getActiveDirectoryFile()));
            activeDirectory.store(out, null);
            out.close();

            ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
            ConfigDB config = (ConfigDB) ctx.getBean("dbConfig");
        } catch (FileNotFoundException e) {
            // todo 500
            throw new NotFoundException("ERROR");
        } catch (IOException e) {
            // todo 500
            throw new NotFoundException("ERROR");
        }
    }
}
