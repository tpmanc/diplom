package controllers;

import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.SettingsModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;


/**
 * Контроллер входа на сайт
 */
@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login() {
        if (UserHelper.isLogin()) {
            return "redirect:/";
        }

        SettingsModel model1 = SettingsModel.findById(SettingsModel.UPLOAD_PATH);
        SettingsModel model2 = SettingsModel.findById(SettingsModel.UPLOAD_REQUEST_PATH);
        if (model1 == null) {
            model1 = new SettingsModel(SettingsModel.UPLOAD_PATH);
        }
        if (model2 == null) {
            model2 = new SettingsModel(SettingsModel.UPLOAD_REQUEST_PATH);
        }
        if (model1.getValue().equals("") || model2.getValue().equals("")) {
            return "redirect:/init-settings";
        }
//        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
//        IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
//        if (!isFilled.isFilled()) {
//            return "redirect:/init-settings";
//        }

        return "login";
    }

    @RequestMapping("/logout")
    public String logout() {
        if (!UserHelper.isLogin()) {
            throw new NotFoundException("Страница не найдена");
        }

        return "redirect:/login";
    }
}