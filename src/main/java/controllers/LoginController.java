package controllers;

import helpers.ConfigDB;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Контроллер входа на сайт
 */
@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login(ServletContext servletContext) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(servletContext.getRealPath("/WEB-INF/config/database.properties"));
            Properties properties = new Properties();
            properties.load(in);
            if (properties.getProperty("db.dbPassword").equals("")) {
                return "redirect:/init-settings";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "admin/login";
    }
}