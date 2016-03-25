package controllers;

import exceptions.NotFoundException;
import helpers.UserHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


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