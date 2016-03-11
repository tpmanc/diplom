package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер входа на сайт
 */
@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login() {
        return "admin/login";
    }
}