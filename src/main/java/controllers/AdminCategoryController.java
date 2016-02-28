package controllers;

import models.CategoryModel;
import org.json.simple.JSONObject;
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
        JSONObject result = new JSONObject();
        try {
            CategoryModel category = new CategoryModel(parent, position, title);
            if (category.add()) {
                result.put("title", title);
                result.put("id", category.getId());
                result.put("error", false);
            } else {
                result.put("error", true);
                result.put("msg", category.errors);
            }
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-rename", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String renameCategory(
            @RequestParam("id") int id,
            @RequestParam("title") String title
    ) {
        JSONObject result = new JSONObject();
        try {
            CategoryModel model = CategoryModel.findById(id);
            model.setTitle(title);
            model.update();
            result.put("title", title);
            result.put("error", false);
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-update-position", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String categoryTreeUpdatePositionUrl(
            @RequestParam("treeId") int id,
            @RequestParam("newParentId") int newParentId,
            @RequestParam("position") int position
            ) {
        JSONObject result = new JSONObject();
        try {
            CategoryModel model = CategoryModel.findById(id);
            if (model.getParent() != newParentId) {
                model.setParent(newParentId);
                model.update();
            }
            CategoryModel.updateSortingOfNode(newParentId, id, position);
            result.put("error", false);
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    @ResponseBody
    @RequestMapping(value = "/category/ajax-delete", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String categoryTreeDeleteUrl(
            @RequestParam("id") int id
    ) {
        JSONObject result = new JSONObject();
        try {
            CategoryModel model = CategoryModel.findById(id);
            model.delete();
            result.put("error", false);
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }
}
