package controllers;

import config.IsFilled;
import config.Settings;
import exceptions.NotFoundException;
import models.SettingsModel;
import models.helpers.ActiveDirectorySettings;
import models.helpers.DatabaseSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;

/**
 * Контроллер настроек приложения
 */
@Controller
public class SettingsController {
    /**
     * Стартовая страница с настройками
     */
    @RequestMapping(value = {"/init-settings" }, method = RequestMethod.GET)
    public String initSettings(Model model) {
        SettingsModel model1 = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
        SettingsModel model2 = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
        if (model1 == null) {
            model1 = new SettingsModel(SettingsModel.UPLOAD_PATH);
        }
        if (model2 == null) {
            model2 = new SettingsModel(SettingsModel.UPLOAD_REQUEST_PATH);
        }
        if (!model1.getValue().equals("") && !model2.getValue().equals("")) {
            throw new NotFoundException("Страница не найдена");
        }
        model.addAttribute("pageTitle", "Настройки");model.addAttribute("model1", model1);
        model.addAttribute("pageTitle", "Настройки");model.addAttribute("model2", model2);

        model.addAttribute("pageTitle", "Настройки");
        return "setting/init-settings";
    }
//
//    @RequestMapping(value = {"/settings-save"}, method = RequestMethod.POST)
//    public String saveSettings(
//            @RequestParam String dbDriver,
//            @RequestParam String dbUrl,
//            @RequestParam String dbUser,
//            @RequestParam String dbPassword,
//            @RequestParam String ldapUrl,
//            @RequestParam String ldapManagerDn,
//            @RequestParam String ldapManagerPass,
//            @RequestParam String ldapUserSearchFilter,
//            @RequestParam String ldapGroupSearch,
//            @RequestParam String ldapGroupSearchFilter,
//            @RequestParam String ldapRoleAttribute
//
//    ) {
//        Settings settings = new Settings();
//        settings.setDatabaseFile(dbDriver, dbUrl, dbUser, dbPassword);
//        settings.save();
//
//        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
//        IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
//        isFilled.setIsNeedRestart(true);
//
//        return "redirect:/init-settings";
//    }
}
