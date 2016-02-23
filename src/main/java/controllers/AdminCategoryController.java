package controllers;

import models.CategoryModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {

    @RequestMapping(value = {"/categories" }, method = RequestMethod.GET)
    public String index(Model model) {
        ArrayList<HashMap> trees = null;
        try {
            trees = CategoryModel.findAll();
            model.addAttribute("trees", trees);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Деревья категорий");
        return "admin/category/categories";
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-add-category", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String addNewCategory(
            @RequestParam("parent") int parent,
            @RequestParam("title") String title,
            @RequestParam("position") int position
    ) {
        String result;
        try {
            CategoryModel category = new CategoryModel(parent, position, title);
            category.add();
            result = "{\"title\":\"" + title + "\", \"id\": \""+category.getId()+"\"}";
        } catch (SQLException e) {
            result = "{\"error\": true}";
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-rename", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String renameCategory(
            @RequestParam("id") int id,
            @RequestParam("title") String title
    ) {
        String result;
        try {
            CategoryModel model = CategoryModel.findById(id);
            model.setTitle(title);
            model.update();
            result = "{\"title\":\"" + title + "\"}";
        } catch (SQLException e) {
            result = "{\"error\": true}";
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-update-position", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String categoryTreeUpdatePositionUrl(
            @RequestParam("treeId") int id,
            @RequestParam("newParentId") int newParentId,
            @RequestParam("position") int position
            ) {
        String result;
        try {
            CategoryModel model = CategoryModel.findById(id);
            if (model.getParent() != newParentId) {
                model.setParent(newParentId);
                model.update();
            }
            CategoryModel.updateSortingOfNode(newParentId, id, position);
            result = "{\"error\": false}";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "{\"error\": true}";
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-delete", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String categoryTreeDeleteUrl(
            @RequestParam("id") String id
    ) {
        // TODO
        String result = "";
//            result = "{\"error\": true}";
        return result;
    }
}
