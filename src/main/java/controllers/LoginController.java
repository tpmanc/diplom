package controllers;

import config.IsFilled;
import config.Settings;
import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.SettingsModel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;

import java.sql.SQLException;


/**
 * Контроллер входа на сайт
 */
@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login(@RequestParam(value="error", required=false, defaultValue = "false") boolean error, Model model) {
        if (UserHelper.isLogin()) {
            return "redirect:/";
        }

        // если не заполнены настройки, то редиректим на страницу с настройками
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
        if (isFilled.isFilled() == null) {
            isFilled.setIsFilled(Settings.isAllFilled());
        }
        if (!isFilled.isFilled()) {
            return "redirect:/init-settings";
        }

        model.addAttribute("error", error);
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