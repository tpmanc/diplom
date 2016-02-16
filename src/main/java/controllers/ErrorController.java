package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ErrorController {
    private Map<Integer, String> errors = new HashMap<Integer, String>();

    private ErrorController() {
        errors.put(400, "Bad Request");
        errors.put(401, "Unauthorized");
        errors.put(403, "Forbidden");
        errors.put(404, "Not Found");
    }

    @RequestMapping("/error")
    public String hello(Model model, @RequestParam(value="code", required = false, defaultValue="0") int errorCode) {

        model.addAttribute("code", errorCode);
        model.addAttribute("codeMessage", errors.get(errorCode));
        model.addAttribute("pageTitle", "");

        return "error";
    }
}
