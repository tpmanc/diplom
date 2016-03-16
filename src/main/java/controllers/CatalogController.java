package controllers;

import exceptions.NotFoundException;
import models.CategoryModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class CatalogController {
    @RequestMapping(value = {"/catalog" }, method = RequestMethod.GET)
    public String users(Model model) {
        try {
            ArrayList<HashMap> trees = CategoryModel.findAll();
            model.addAttribute("trees", trees);
            model.addAttribute("pageTitle", "Каталог файлов");
            return "catalog/catalog";
        } catch (SQLException e) {
            throw new NotFoundException("Страница не найдена");
        }
    }
}
