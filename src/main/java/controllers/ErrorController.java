package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для страниц ошибок
 */
@Controller
public class ErrorController {
    @RequestMapping(value = {"/forbidden" }, method = RequestMethod.GET)
    public String forbidden() {
        return "error403";
    }

    @RequestMapping(value = {"/not-found" }, method = RequestMethod.GET)
    public String notFound() {
        return "error404";
    }

    @RequestMapping(value = {"/internal-error" }, method = RequestMethod.GET)
    public String internalError() {
        return "error500";
    }
}
