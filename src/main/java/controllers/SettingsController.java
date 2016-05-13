package controllers;

import auth.CustomUserDetails;
import config.IsFilled;
import config.Settings;
import exceptions.ForbiddenException;
import exceptions.InternalException;
import helpers.UserHelper;
import models.SettingsModel;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Контроллер настроек приложения
 */
@Controller
public class SettingsController {
    private static final Logger logger = Logger.getLogger(SettingsController.class);

    /**
     * Стартовая страница с настройками
     */
    @RequestMapping(value = {"/init-settings" }, method = RequestMethod.GET)
    public String initSettings(Model model) {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
        if (isFilled.isFilled() == null) {
            isFilled.setIsFilled(Settings.isAllFilled());
        }
        if (isFilled.isFilled()) {
            return "redirect:/";
        }

        try {
            SettingsModel model1 = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
            SettingsModel model2 = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
            if (model1 == null) {
                model1 = new SettingsModel(SettingsModel.UPLOAD_PATH);
            }
            if (model2 == null) {
                model2 = new SettingsModel(SettingsModel.UPLOAD_REQUEST_PATH);
            }
            model.addAttribute("model1", model1);
            model.addAttribute("model2", model2);
        } catch (Exception e) {
            model.addAttribute("noDatabaseFound", true);
        }

        HashMap<String, String> dbProperties = Settings.getDbProperties();
        model.addAttribute("dbProperties", dbProperties);
        String dbFilePath = Settings.getDbPath();
        model.addAttribute("dbFilePath", dbFilePath);

        HashMap<String, String> adProperties = Settings.getADProperties();
        model.addAttribute("adProperties", adProperties);
        String adFilePath = Settings.getADPath();
        model.addAttribute("adFilePath", adFilePath);

        HashMap<String, String> logProperties = Settings.getLogProperties();
        String loggers = logProperties.get(Settings.loggersProperty);
        boolean isLogFileEnabled = false;
        boolean isLogSyslogEnabled = false;
        if (loggers != null) {
            if (loggers.contains(Settings.fileLoggerName)) {
                isLogFileEnabled = true;
            }
            if (loggers.contains(Settings.syslogLoggerName)) {
                isLogSyslogEnabled = true;
            }
        }
        model.addAttribute("isLogFileEnabled", isLogFileEnabled);
        model.addAttribute("isLogSyslogEnabled", isLogSyslogEnabled);
        model.addAttribute("logProperties", logProperties);
        String logFilePath = Settings.getLogPath();
        model.addAttribute("logFilePath", logFilePath);

        model.addAttribute("pageTitle", "Настройки");
        return "setting/init-settings";
    }

    @RequestMapping(value = {"/settings" }, method = RequestMethod.GET)
    public String settings(Model model, Principal principal) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка доступа к настройкам (/settings) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        SettingsModel model1 = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
        SettingsModel model2 = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
        if (model1 == null) {
            model1 = new SettingsModel(SettingsModel.UPLOAD_PATH);
        }
        if (model2 == null) {
            model2 = new SettingsModel(SettingsModel.UPLOAD_REQUEST_PATH);
        }
        model.addAttribute("model1", model1);
        model.addAttribute("model2", model2);

        HashMap<String, String> dbProperties = Settings.getDbProperties();
        model.addAttribute("dbProperties", dbProperties);

        HashMap<String, String> adProperties = Settings.getADProperties();
        model.addAttribute("adProperties", adProperties);

        model.addAttribute("pageTitle", "Настройки");
        return "setting/settings";
    }

    @RequestMapping(value = {"/settings-save" }, method = RequestMethod.POST)
    public String settingsInitSave(
            @RequestParam String catalogPath,
            @RequestParam String requestPath,
            @RequestParam String dbUrl,
            @RequestParam String dbUser,
            @RequestParam(value="dbPass", required=false) String dbPass,
            @RequestParam(value="dbPool", required=true) Integer dbPool,
            @RequestParam String ldapUrl,
            @RequestParam String ldapManager,
            @RequestParam String ldapPassword,
            @RequestParam String ldapUserFilter,
            @RequestParam String ldapGroupSearch,
            @RequestParam String ldapGroupFilter,
            @RequestParam String ldapRole,
            @RequestParam(value="logFileEnable", required=false, defaultValue = "false") boolean logFileEnable,
            @RequestParam(value="logFilePath", required=false) String logFilePath,
            @RequestParam(value="logFileCount", required=false) Integer logFileCount,
            @RequestParam(value="logFileMaxSize", required=false) Integer logFileMaxSize,
            @RequestParam(value="logSyslogEnable", required=false, defaultValue = "false") boolean logSyslogEnable,
            @RequestParam(value="logSyslogHost", required=false) String logSyslogHost,
            @RequestParam(value="logSyslogFacility", required=false) String logSyslogFacility,
            RedirectAttributes attr,
            @RequestParam(value="isFromPanel", required=false, defaultValue = "false") boolean isFromPanel,
            @RequestParam(value="isNoDatabase", required=false, defaultValue = "false") boolean isNoDatabase
    ) {
        HashMap<String, String> errors = new HashMap<>();

        if (!isNoDatabase) {
            File path1 = new File(catalogPath);
            if (!path1.exists() || !path1.isDirectory()) {
                errors.put("catalogPath", "Такой путь не найден");
            } else {
                if (!path1.canRead()) {
                    errors.put("catalogPath", "Нет прав на чтение");
                } else if (!path1.canWrite()) {
                    errors.put("catalogPath", "Нет прав на запись");
                } else {
                    SettingsModel catalogModel = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
                    if (catalogModel != null) {
                        catalogModel.setValue(path1.getAbsolutePath());
                        try {
                            catalogModel.update();
                        } catch (SQLException e) {
                            throw new InternalException("Ошибка при сохранении пути в БД");
                        }
                    }

                }
            }

            File path2 = new File(requestPath);
            if (!path2.exists() || !path2.isDirectory()) {
                errors.put("requestPath", "Такой путь не найден");
            } else {
                if (!path2.canRead()) {
                    errors.put("requestPath", "Нет прав на чтение");
                } else if (!path2.canWrite()) {
                    errors.put("requestPath", "Нет прав на запись");
                } else {
                    SettingsModel requestModel = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
                    if (requestModel != null) {
                        requestModel.setValue(path2.getAbsolutePath());
                        try {
                            requestModel.update();
                        } catch (SQLException e) {
                            throw new InternalException("Ошибка при сохранении пути в БД");
                        }
                    }
                }
            }
        }

        boolean logFileError = false;
        if (logFileEnable) {
            File logFile = new File(logFilePath);
            if (!logFile.getParentFile().exists() || !logFile.getParentFile().isDirectory()) {
                errors.put("logFilePath", "Такой путь не найден");
            } else {
                try {
                    boolean isFileCreated = true;
                    if (!logFile.exists() && !logFile.isDirectory()) {
                        isFileCreated = logFile.createNewFile();
                    }
                    if (!isFileCreated) {
                        logFileError = true;
                        errors.put("logFilePath", "Невозможно создать файл");
                    } else {
                        if (!logFile.canRead()) {
                            logFileError = true;
                            errors.put("logFilePath", "Нет прав на чтение");
                        } else if (!logFile.canWrite()) {
                            logFileError = true;
                            errors.put("logFilePath", "Нет прав на запись");
                        }
                    }
                } catch (IOException e) {
                    logFileError = true;
                    errors.put("logFilePath", "Невозможно создать файл");
                }
            }

            if (logFileCount != null && logFileCount <= 0) {
                logFileError = true;
                errors.put("logFileCount", "Количество файлов должно быть больше 0");
            }
            if (logFileMaxSize <= 0) {
                logFileError = true;
                errors.put("logFileMaxSize", "Максимальный размер файла должен быть больше 0");
            }

            if (!logFileError) {
                 Settings.setFileProperties(logFilePath, logFileCount, logFileMaxSize);
            }
        } else {
            Settings.disableFileLogger();
        }

        boolean logSyslogError = false;
        if (logSyslogEnable) {
            if (logSyslogHost != null && logSyslogHost.length() <= 0) {
                logSyslogError = true;
                errors.put("logSyslogHost", "Необходимо указать адрес");
            }
            if (logSyslogFacility != null && logSyslogFacility.length() <= 0) {
                logSyslogError = true;
                errors.put("logSyslogFacility", "Необходимо указать категорию");
            }
            if (!logSyslogError) {
                Settings.setSyslogProperties(logSyslogHost, logSyslogFacility);
                // todo Settings.addSyslogLogging(logSyslogHost, logSyslogFacility);
            }
        } else {
            Settings.disableSyslogLogger();
        }

        Settings.setDbProperties(dbUrl, dbUser, dbPass, dbPool);

        Settings.setAdProperties(ldapUrl, ldapManager, ldapPassword, ldapUserFilter, ldapGroupSearch, ldapGroupFilter, ldapRole);

        if (errors.size() > 0) {
            attr.addFlashAttribute("errors", errors);
            if (isFromPanel) {
                return "redirect:/settings";
            } else {
                return "redirect:/init-settings";
            }
        }
        if (isFromPanel) {
            attr.addFlashAttribute("isNeedRestart", true);
            return "redirect:/settings";
        } else {
            attr.addFlashAttribute("isNeedRestart", true);
            return "redirect:/init-settings";
        }
    }
}
