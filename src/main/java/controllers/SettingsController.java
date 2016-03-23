package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Контроллер настроек приложения
 */
@Controller
public class SettingsController {
    /**
     * Страница с настройками
     */
    @RequestMapping(value = {"/init-settings" }, method = RequestMethod.GET)
    public String initSettings(ServletContext servletContext, Model model) {
        FileOutputStream out = null;
        FileInputStream in = null;
        Properties properties = new Properties();
        try {
            in = new FileInputStream(servletContext.getRealPath("/WEB-INF/config/database.properties"));
            out = new FileOutputStream(servletContext.getRealPath("/WEB-INF/config/database.properties"));
            properties.load(in);

            if (properties.getProperty("db.dbPassword").equals("")) {
                properties.setProperty("db.dbPassword", "pass");
            }
            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Настройки");
        return "setting/settings";
    }
}
