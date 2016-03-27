package controllers;

import auth.CustomUserDetails;
import exceptions.NotFoundException;
import models.CategoryModel;
import models.FileModel;
import models.RequestModel;
import models.helpers.CategoryFile;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class CatalogController {
    @RequestMapping(value = {"/catalog", "/"}, method = RequestMethod.GET)
    public String users(
            @RequestParam(value="categoryId", required=false, defaultValue = "0") int categoryId,
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Principal principal,
            Model model
    ) {
        int limit = FileModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        try {
            ArrayList<HashMap> trees = CategoryModel.findAll();
            model.addAttribute("trees", trees);

            if (categoryId > 0) {
                CategoryModel category = CategoryModel.findById(categoryId);
                model.addAttribute("category", category);

                ArrayList<CategoryFile> categoryFiles = category.getFiles(limit, offset);
                model.addAttribute("categoryFiles", categoryFiles);
            }

            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
            model.addAttribute("requestCount", requestCount);

            model.addAttribute("categoryId", categoryId);
            model.addAttribute("pageTitle", "Каталог файлов");
            return "catalog/catalog";
        } catch (SQLException e) {
            throw new NotFoundException("Страница не найдена");
        }
    }

    @ResponseBody
    @RequestMapping(value = {"/ajax-load-files" }, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String ajaxLoadFiles(@RequestParam("categoryId") int categoryId) {
        JSONObject result = new JSONObject();

        return result.toJSONString();
    }
}
