package controllers;

import config.IsFilled;
import config.Settings;
import exceptions.NotFoundException;
import helpers.UserHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;

import java.security.Principal;


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
//        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
//        IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
//        if (!isFilled.isFilled()) {
//            return "redirect:/init-settings";
//        }

        return "admin/login";
    }

    @RequestMapping("/logout")
    public String logout() {
        if (!UserHelper.isLogin()) {
            throw new NotFoundException("Страница не найдена");
        }

        return "redirect:/login";
    }
}