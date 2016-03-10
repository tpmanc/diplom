package controllers;

import exceptions.CustomWebException;
import models.FileModel;
import models.FilePropertyModel;
import models.PropertyModel;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminFilePropertyController {
    @RequestMapping(value = {"/file-property-add" }, method = RequestMethod.GET)
    public String fileAddProperty(@RequestParam("id") int id, Model model) {
        try {
            FileModel file = FileModel.findById(id);
            model.addAttribute("file", file);

            ArrayList<HashMap> properties = PropertyModel.findAllNotUsedCustom(file.getId());
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            throw new CustomWebException("Файл не существует");
        }

        model.addAttribute("pageTitle", "Добавить свойство");
        return "admin/file-property/file-property-add";
    }

    @RequestMapping(value = {"/file-property-edit" }, method = RequestMethod.GET)
    public String fileEditProperty(@RequestParam("id") int id, Model model) {
        try {
            FilePropertyModel fileProperty = FilePropertyModel.findById(id);
            model.addAttribute("fileProperty", fileProperty);
        } catch (SQLException e) {
            throw new CustomWebException("Свойство файла не существует");
        }

        model.addAttribute("pageTitle", "Изменить свойство файла");
        return "admin/file-property/file-property-edit";
    }

    @ResponseBody
    @RequestMapping(value = {"/file-property-delete"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String filePropertyDelete(@RequestParam("propertyLink") int propertyLink) {
        JSONObject result = new JSONObject();
        boolean error = true;

        try {
            FilePropertyModel fileProperty = FilePropertyModel.findById(propertyLink);
            if (fileProperty.delete()) {
                error = false;
            }
        } catch (SQLException e) {
            throw new CustomWebException("Свойство файла не найдено");
        }

        result.put("error", error);
        return result.toJSONString();
    }

    @RequestMapping(value = {"/file-property-handler" }, method = RequestMethod.POST)
    public String fileAddPropertyHandler(
            @RequestParam("fileId") int fileId,
            @RequestParam("propertyId") int propertyId,
            @RequestParam("value") String value,
            @RequestParam(value="id", required=false, defaultValue = "0") int id
    ) {
        if (id == 0) {
            FilePropertyModel fileProperty = new FilePropertyModel(fileId, propertyId, value);
            try {
                if (fileProperty.add()) {
                    return "redirect:/admin/file-view?id="+fileId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FilePropertyModel fileProperty = FilePropertyModel.findById(id);
                fileProperty.setValue(value);
                if (fileProperty.update()) {
                    return "redirect:/admin/file-view?id="+fileId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "admin/file-property/file-property-add?id="+fileId;
    }
}
