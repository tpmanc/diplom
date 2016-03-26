package controllers;

import models.FileModel;
import models.FileVersionModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Контроллер незаполненных файлов для администратора
 */
@Controller
@RequestMapping("/admin")
public class AdminUnfilledFile {
    @RequestMapping(value = {"/unfilled-files-all" }, method = RequestMethod.GET)
    public String filesUnfilled(@RequestParam(value="page", required=false, defaultValue = "1") int page, Model model, Principal principal) {
        int limit = FileModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        ArrayList<HashMap> unfilledFiles = FileVersionModel.findUnfilled(limit, offset);
        model.addAttribute("files", unfilledFiles);

        int pageCount = (int) Math.ceil((float)FileVersionModel.getUnfilledCount() / limit);
        model.addAttribute("pageCount", pageCount);

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Все незаполненные файлы");
        return "unfilled-file/files";
    }
}
