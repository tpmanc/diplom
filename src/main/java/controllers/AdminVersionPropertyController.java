package controllers;

import exceptions.NotFoundException;
import models.*;
import org.json.simple.JSONObject;
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

/**
 * Контроллер свойств версии для администратора
 */
@Controller
@RequestMapping("/admin")
public class AdminVersionPropertyController {
    /**
     * Добавление свойства версии
     * @param id Id версии
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-version-property-add" }, method = RequestMethod.GET)
    public String fileAddProperty(@RequestParam("id") int id, Model model) {
        try {
            FileVersionModel fileVersion = FileVersionModel.findById(id);
            model.addAttribute("fileVersion", fileVersion);

            ArrayList<HashMap> properties = PropertyModel.findAllNotUsedCustom(fileVersion.getId(), true);
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            throw new NotFoundException("Версия не существует");
        }

        model.addAttribute("pageTitle", "Добавить свойство");
        return "file-version-property/file-version-property-add";
    }

    /**
     * Изменение значения свойства версии
     * @param id Id свойства версии
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-version-property-edit" }, method = RequestMethod.GET)
    public String fileEditProperty(@RequestParam("id") int id, Model model) {
        try {
            FileVersionPropertyModel fileProperty = FileVersionPropertyModel.findById(id);
            model.addAttribute("fileProperty", fileProperty);

            FileVersionModel fileVersion = FileVersionModel.findById(fileProperty.getFileVersionId());
            model.addAttribute("fileVersion", fileVersion);
        } catch (SQLException e) {
            throw new NotFoundException("Свойство версии не найдено");
        }

        model.addAttribute("pageTitle", "Изменить свойство версии");
        return "file-version-property/file-version-property-edit";
    }

    /**
     * Обработчик ajax запроса на удаления свойства версии
     * @param propertyLink Id свойства версии
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = {"/file-version-property-delete"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String filePropertyDelete(@RequestParam("propertyLink") int propertyLink) {
        JSONObject result = new JSONObject();
        boolean error = true;

        try {
            FileVersionPropertyModel fileProperty = FileVersionPropertyModel.findById(propertyLink);
            if (fileProperty.delete()) {
                error = false;
            }
        } catch (SQLException e) {
            throw new NotFoundException("Свойство файла не найдено");
        }

        result.put("error", error);
        return result.toJSONString();
    }

    /**
     * Обработчик добавления и изменения свойства версии
     * @param fileVersionId Id версии
     * @param propertyId Id свойства
     * @param value Значение свойства
     * @param id Id свойства версии
     * @return Перенаправление
     */
    @RequestMapping(value = {"/file-version-property-handler" }, method = RequestMethod.POST)
    public String fileAddPropertyHandler(
            @RequestParam("fileVersionId") int fileVersionId,
            @RequestParam("propertyId") int propertyId,
            @RequestParam("value") String value,
            @RequestParam(value="id", required=false, defaultValue = "0") int id,
            RedirectAttributes attr
    ) {
        // проверяем, есть ли такое свойство
        PropertyModel property = PropertyModel.findById(propertyId);
        // проверяем, не заполнено ли уже это свойство
        FileVersionPropertyModel checkProperty = FileVersionPropertyModel.isPropertyExist(fileVersionId, propertyId);
        if (checkProperty != null) {
            id = checkProperty.getId();
        }
        // если это добавление нового свойства версии
        if (id == 0) {
            try {
                FileVersionModel fileVersion = FileVersionModel.findById(fileVersionId);
                FileVersionPropertyModel fileProperty = new FileVersionPropertyModel(fileVersion.getId(), propertyId, value);
                if (fileProperty.add()) {
                    return "redirect:/file-view?id="+fileVersion.getFileId()+"&versionId="+fileVersion.getId();
                } else {
                    attr.addFlashAttribute("errors", fileProperty.errors);
                    attr.addFlashAttribute("selectedProperty", propertyId);
                    return "redirect:/admin/file-version-property-add?id="+fileVersionId;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Ошибка сервера");
            }
        } else {
            // если это изменение значения свойства версии
            try {
                FileVersionModel fileVersion = FileVersionModel.findById(fileVersionId);
                FileVersionPropertyModel fileProperty = FileVersionPropertyModel.findById(id);
                fileProperty.setValue(value);
                if (fileProperty.update()) {
                    return "redirect:/file-view?id="+fileVersion.getFileId()+"&versionId="+fileVersion.getId();
                } else {
                    attr.addFlashAttribute("errors", fileProperty.errors);
                    return "redirect:/admin/file-version-property-edit?id="+id;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Ошибка сервера");
            }
        }
    }
}
