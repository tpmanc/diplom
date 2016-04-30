package controllers;

import exceptions.NotFoundException;
import models.SettingsModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
