package controllers;

import exceptions.NotFoundException;
import models.CategoryModel;
import models.FileModel;
import models.helpers.CategoryFile;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class SearchController {
    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String users(
            @RequestParam String text,
            Model model
    ) {
        ArrayList<CategoryFile> files = FileModel.findFilesByTitles(text);
        model.addAttribute("files", files);

        model.addAttribute("searchText", text);
        model.addAttribute("pageTitle", "Результаты поиска \"" + text + "\"");
        return "search/search";
    }
}
