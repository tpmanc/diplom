package controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(@RequestParam(value="name", required=false) String name, Model model) {
        model.addAttribute("pageTitle", "Index");

        return "admin/index";
    }
}
