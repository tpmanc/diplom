package controllers;

import config.Settings;
import models.helpers.ActiveDirectorySettings;
import models.helpers.DatabaseSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;

/**
 * Контроллер настроек приложения
 */
@Controller
public class SettingsController {
    /**
     * Страница с настройками
     */
    @RequestMapping(value = {"/init-settings" }, method = RequestMethod.GET)
    public String initSettings(Model model) {
        Settings settings = new Settings();

        DatabaseSettings databaseSettings = settings.getDatabaseSettings();
        model.addAttribute("database", databaseSettings);

        ActiveDirectorySettings activeDirectorySettings = settings.getActiveDirectorySettings();
        model.addAttribute("activeDirectory", activeDirectorySettings);

        model.addAttribute("pageTitle", "Настройки");
        return "setting/settings";
    }

    @RequestMapping(value = {"/settings-save"}, method = RequestMethod.POST)
    public String saveSettings(
            @RequestParam String dbDriver,
            @RequestParam String dbUrl,
            @RequestParam String dbUser,
            @RequestParam String dbPassword,
            @RequestParam String ldapUrl,
            @RequestParam String ldapManagerDn,
            @RequestParam String ldapManagerPass,
            @RequestParam String ldapUserSearchFilter,
            @RequestParam String ldapGroupSearch,
            @RequestParam String ldapGroupSearchFilter,
            @RequestParam String ldapRoleAttribute,
            RedirectAttributes attr

    ) {
        Settings settings = new Settings();
        settings.setDatabaseFile(dbDriver, dbUrl, dbUser, dbPassword);
        settings.setActiveDirectoryFile(ldapUrl, ldapManagerDn, ldapManagerPass, ldapUserSearchFilter, ldapGroupSearch, ldapGroupSearchFilter, ldapRoleAttribute);
        settings.save();

        attr.addFlashAttribute("isSaved", true);
        return "redirect:/init-settings";
    }
}
