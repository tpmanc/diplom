package controllers;

import exceptions.CustomWebException;
import models.CategoryModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class CategoryController {

    @RequestMapping(value = {"/categories" }, method = RequestMethod.GET)
    public String index(Model model) {
        ArrayList<HashMap> categories = null;
        try {
            categories = CategoryModel.findAll();
            model.addAttribute("categories", categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Категории");
        return "category/categories";
    }

    @RequestMapping(value = {"/category-add" }, method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("pageTitle", "Добавить категорию");
        return "category/category-add";
    }

    @RequestMapping(value = {"/category-edit" }, method = RequestMethod.GET)
    public String update(@RequestParam("id") int id, Model model) {
        CategoryModel category = null;
        try {
            category = CategoryModel.findById(id);
        } catch (SQLException e) {
            throw new CustomWebException("Категория не существует");
        }
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Изменить категорию");
        return "category/category-edit";
    }

    @RequestMapping(value = {"/category-add-handler" }, method = RequestMethod.POST)
    public String addHandler(
            @RequestParam("title") String title,
            @RequestParam(value="isEnabled", required=true, defaultValue="false") boolean isEnabled,
            RedirectAttributes attr,
            Model model
    ) {
        CategoryModel category = new CategoryModel(title, isEnabled);
        try {
            if (category.add()) {
                return "redirect:/categories";
            } else {
                attr.addFlashAttribute("errors", category.errors);
                return "redirect:/category-add";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/category-add";
    }

    @RequestMapping(value = {"/category-edit-handler" }, method = RequestMethod.POST)
    public String updateHandler(
            @RequestParam("id") int id,
            @RequestParam("title") String title,
            @RequestParam(value="isEnabled", required=true, defaultValue="false") boolean isEnabled,
            RedirectAttributes attr,
            Model model
    ) {
        CategoryModel category = null;
        try {
            category = CategoryModel.findById(id);
            category.setTitle(title);
            category.setIsEnabled(isEnabled);
            if (category.update()) {
                return "redirect:/categories";
            } else {
                attr.addFlashAttribute("errors", category.errors);
                return "redirect:/category-edit?id=" + id;
            }
        } catch (SQLException e) {

        }
        return "redirect:/category-edit?id=" + id;
    }

    @RequestMapping(value = {"/category-delete" }, method = RequestMethod.POST)
    public @ResponseBody boolean deleteHandler(
            @RequestParam("categoryId") int categoryId,
            Model model
    ) {
        try {
            CategoryModel category = CategoryModel.findById(categoryId);
            if (category.delete()) {
                return true;
            }
        } catch (SQLException e) {

        }
        return false;
    }

}
