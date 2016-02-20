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
@RequestMapping("/admin")
public class AdminCategoryTreeController {

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
        return "admin/category-tree/category-trees";
    }

    @ResponseBody
    @RequestMapping(value = "/category-tree/ajax-add-category", method = RequestMethod.POST, produces = "application/json")
    public String addNewCategory(
            @RequestParam("parent") String parent,
            @RequestParam("treeId") String treeId,
            @RequestParam("title") String title,
            @RequestParam("position") int soring
    ) {
        String result;
        try {
            CategoryModel category = new CategoryModel(title, true);
            category.add();
            CategoryTreeModel treeElem = new CategoryTreeModel(parent, treeId, category.getId(), soring);
            treeElem.add();
            result = "{\"title\":\"" + title + "\"}";
        } catch (SQLException e) {
            result = "{\"error\": true}";
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/category-tree/ajax-update-position", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeUpdatePositionUrl(
            @RequestParam("treeId") String treeId,
            @RequestParam("newParentId") String newParentId,
            @RequestParam("position") int position
            ) {
        String result;
        try {
            CategoryTreeModel model = CategoryTreeModel.findById(treeId);
            if (!model.getParent().equals(newParentId)) {
                model.setParent(newParentId);
                model.update();
            }
            CategoryTreeModel.updateSortingOfNode(newParentId ,treeId, position);
            result = "{\"error\": false}";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "{\"error\": true}";
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/category-tree/ajax-delete", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeDeleteUrl(
            @RequestParam("treeId") String treeId
    ) {
        String result;
        try {
            CategoryTreeModel model = CategoryTreeModel.findById(treeId);
            CategoryModel category = CategoryModel.findById(model.getCategoryId());
            category.delete();
            model.delete();
            result = "{\"error\": false}";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "{\"error\": true}";
        }
        return result;
    }
}
