package controllers;

import config.Settings;
import helpers.UserHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;


/**
 * Контроллер входа на сайт
 */
@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login() {
        if (UserHelper.isLogin()) {
//            return "redirect:/";
        }
        Settings settings = new Settings();
        if (!settings.isFilled()) {
            return "redirect:/init-settings";
        }

        return "admin/login";
    }
}