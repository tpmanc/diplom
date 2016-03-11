package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Контроллер незаполненных файлов для администратора
 */
@Controller
@RequestMapping("/admin")
public class AdminUnfilledFile {
    @RequestMapping(value = {"/unfilled-files" }, method = RequestMethod.GET)
    public String filesUnfilled(Model model) {
        // TODO

        model.addAttribute("pageTitle", "Незаполненные файлы");
        return "admin/unfilled-file/files";
    }
}
