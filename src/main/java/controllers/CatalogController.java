package controllers;

import auth.CustomUserDetails;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.*;
import models.helpers.CategoryFile;
import models.helpers.FileCategory;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            ArrayList<CategoryModel> trees = CategoryModel.findAll();
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
}
