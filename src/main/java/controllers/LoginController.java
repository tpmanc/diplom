package controllers;

import config.IsFilled;
import config.Settings;
import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.SettingsModel;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;


/**
 * Контроллер входа на сайт
 */
@Controller
public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class);

    @RequestMapping("/login")
    public String login(@RequestParam(value="error", required=false, defaultValue = "false") boolean error, Model model) {
        if (UserHelper.isLogin()) {
            return "redirect:/";
        }

        logger.info("This is an info log entry");

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