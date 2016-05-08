package controllers;

import auth.CustomUserDetails;
import models.FileModel;
import models.RequestModel;
import models.helpers.CategoryFile;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class SearchController {
    private static final Logger logger = Logger.getLogger(SearchController.class);

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String users(
            @RequestParam String text,
            Principal principal,
            Model model
    ) {
        ArrayList<CategoryFile> files = FileModel.findFilesByTitles(text);
        model.addAttribute("files", files);

        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
        model.addAttribute("requestCount", requestCount);

        model.addAttribute("searchText", text);
        model.addAttribute("pageTitle", "Результаты поиска \"" + text + "\"");

        logger.warn("Поиск файла, поисковый запрос: " + text+"; служебный номер - "+activeUser.getEmployeeId());
        return "search/search";
    }
}
