package controllers;

import exceptions.CustomWebException;
import models.CategoryModel;
import models.CategoryTreeModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class CategoryTreeController {

    @RequestMapping(value = {"/category-trees" }, method = RequestMethod.GET)
    public String index(Model model) {
        ArrayList<HashMap> trees = null;
        try {
            trees = CategoryTreeModel.findAll();
            model.addAttribute("trees", trees);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Деревья категорий");
        return "category-tree/category-trees";
    }

    @ResponseBody
    @RequestMapping(value = "/category-tree/ajax-save", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeSaveUrl(
            @RequestParam("parent") String parent,
            @RequestParam("treeId") String treeId,
            @RequestParam(value="categoryId", required=false) Integer categoryId,
            @RequestParam(value="title", required=false) String title
    ) {
        CategoryTreeModel treeElem = new CategoryTreeModel(parent, treeId, title);
        String result;
        try {
            // TODO: сохранение строки в таблицу
            treeElem.add();
            result = "{\"title\":\"" + title + "\"}";
            return result;
        } catch (SQLException e) {
            result = "{\"error\": true}";
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/category-tree/ajax-update", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeUpdateUrl(
            @RequestParam("treeId") String treeId
            ) {
        // TODO: изменение позиции и инфы по treeId
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/category-tree/ajax-delete", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeDeleteUrl(
            @RequestParam("treeId") String treeId
    ) {
        String result;
        try {
            CategoryTreeModel model = CategoryTreeModel.findById(treeId);
            model.delete();
            result = "{\"error\": false}";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "{\"error\": true}";
        }
        return result;
    }
}
