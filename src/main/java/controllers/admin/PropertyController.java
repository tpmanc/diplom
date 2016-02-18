package controllers.admin;

import exceptions.CustomWebException;
import models.PropertyModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class PropertyController {

    @RequestMapping(value = {"/properties" }, method = RequestMethod.GET)
    public String index(Model model) {
        ArrayList<HashMap> properties = null;
        try {
            properties = PropertyModel.findAll();
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Свойства файлов");
        return "property/properties";
    }

    @RequestMapping(value = {"/property-add" }, method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("pageTitle", "Добавить свойство файла");
        return "property/property-add";
    }

    @RequestMapping(value = {"/property-edit" }, method = RequestMethod.GET)
    public String update(@RequestParam("id") int id, Model model) {
        PropertyModel property = null;
        try {
            property = PropertyModel.findById(id);
        } catch (SQLException e) {
            throw new CustomWebException("Свойство не найдено");
        }
        model.addAttribute("property", property);
        model.addAttribute("pageTitle", "Изменить свойство файла");
        return "property/property-edit";
    }

    @RequestMapping(value = {"/property-view" }, method = RequestMethod.GET)
    public String view(@RequestParam("id") int id, Model model) {
        PropertyModel property = null;
        try {
            property = PropertyModel.findById(id);
        } catch (SQLException e) {
            throw new CustomWebException("Свойство не найдено");
        }
        model.addAttribute("property", property);
        model.addAttribute("pageTitle", "Просмотр свойства");
        return "property/property-view";
    }

    @RequestMapping(value = {"/property-add-handler" }, method = RequestMethod.POST)
    public String addHandler(
            @RequestParam("title") String title,
            RedirectAttributes attr,
            Model model
    ) {
        PropertyModel property = new PropertyModel(title);
        try {
            if (property.add()) {
                return "redirect:/properties";
            } else {
                attr.addFlashAttribute("errors", property.errors);
                return "redirect:/property-add";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/property-add";
    }

    @RequestMapping(value = {"/property-edit-handler" }, method = RequestMethod.POST)
    public String updateHandler(
            @RequestParam("id") int id,
            @RequestParam("title") String title,
            RedirectAttributes attr,
            Model model
    ) {
        PropertyModel property = null;
        try {
            property = PropertyModel.findById(id);
            property.setTitle(title);
            if (property.update()) {
                return "redirect:/properties";
            } else {
                attr.addFlashAttribute("errors", property.errors);
                return "redirect:/property-edit?id=" + id;
            }
        } catch (SQLException e) {

        }
        return "redirect:/property-edit?id=" + id;
    }

    @RequestMapping(value = {"/property-delete" }, method = RequestMethod.POST)
    public @ResponseBody boolean deleteHandler(
            @RequestParam("id") int id,
            Model model
    ) {
        // TODO: обработка удаления свойства
        return false;
    }

}
