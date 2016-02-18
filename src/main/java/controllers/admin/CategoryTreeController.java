package controllers.admin;

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
@RequestMapping(value = {"/admin/*"})
public class CategoryTreeController {

    @RequestMapping(value = {"category-trees" }, method = RequestMethod.GET)
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
    @RequestMapping(value = "category-tree/ajax-save", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeSaveUrl(
            @RequestParam("parent") String parent,
            @RequestParam("treeId") String treeId,
            @RequestParam("title") String title
    ) {
        CategoryTreeModel treeElem = new CategoryTreeModel(parent, treeId, title);
        String result;
        try {
            treeElem.add();
            result = "{\"title\":\"" + title + "\"}";
        } catch (SQLException e) {
            result = "{\"error\": true}";
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "category-tree/ajax-add-category", method = RequestMethod.POST, produces = "application/json")
    public String addNewCategory(
            @RequestParam("parent") String parent,
            @RequestParam("treeId") String treeId,
            @RequestParam("title") String title
    ) {
        String result;
        try {
            CategoryModel category = new CategoryModel(title, true);
            category.add();
            CategoryTreeModel treeElem = new CategoryTreeModel(parent, treeId, category.getId());
            treeElem.add();
            result = "{\"title\":\"" + title + "\"}";
        } catch (SQLException e) {
            result = "{\"error\": true}";
            e.printStackTrace();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "category-tree/ajax-update", method = RequestMethod.POST, produces = "application/json")
    public String categoryTreeUpdateUrl(
            @RequestParam("treeId") String treeId
            ) {
        // TODO: изменение позиции и инфы по treeId
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "category-tree/ajax-delete", method = RequestMethod.POST, produces = "application/json")
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
