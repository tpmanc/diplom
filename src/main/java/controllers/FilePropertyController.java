package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.FileModel;
import models.FilePropertyModel;
import models.PropertyModel;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Контроллер свойств файла для модератора
 */
@Controller
public class FilePropertyController {
    private static final Logger logger = Logger.getLogger(FilePropertyController.class);

    /**
     * Добавление свойства к файлу
     * @param id Id файла
     * @param model
     * Путь до представления
     */
    @RequestMapping(value = {"/file-property-add" }, method = RequestMethod.GET)
    public String fileAddProperty(
            @RequestParam("id") int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка добавления свойства файла (/file-property-add) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            FileModel file = FileModel.findById(id);
            model.addAttribute("file", file);

            ArrayList<PropertyModel> properties = PropertyModel.findAllNotUsedCustom(file.getId(), false);
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            throw new NotFoundException("Файл не существует");
        }

        model.addAttribute("pageTitle", "Добавить свойство");
        return "file-property/file-property-add";
    }

    /**
     * Изменение значения свойства файла
     * @param id Id файла
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-property-edit" }, method = RequestMethod.GET)
    public String fileEditProperty(
            @RequestParam("id") int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка изменения свойства файла (/file-property-edit) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        try {
            FilePropertyModel fileProperty = FilePropertyModel.findById(id);
            model.addAttribute("fileProperty", fileProperty);
        } catch (SQLException e) {
            throw new NotFoundException("Свойство файла не существует");
        }

        model.addAttribute("pageTitle", "Изменить свойство файла");
        return "file-property/file-property-edit";
    }

    /**
     * Обработчик ajax запроса на удаление свойства файла
     * @param propertyLink Id свойства файла
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = {"/file-property-delete"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String filePropertyDelete(
            @RequestParam("propertyLink") int propertyLink,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка удаления свойства файла (/file-property-delete) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();
        boolean error = true;

        try {
            FilePropertyModel fileProperty = FilePropertyModel.findById(propertyLink);
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
     * Обработчик добавление и изменения сайоства файла
     * @param fileId Id файла
     * @param propertyId Id свойства
     * @param value Значение свойства
     * @param id Id свойства файла
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-property-handler" }, method = RequestMethod.POST)
    public String fileAddPropertyHandler(
            @RequestParam("fileId") int fileId,
            @RequestParam("propertyId") int propertyId,
            @RequestParam("value") String value,
            @RequestParam(value="id", required=false, defaultValue = "0") int id,
            RedirectAttributes attr,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка добавления/изменения свойства файла (/file-property-handler) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        // проверяем, есть ли такое свойство
        PropertyModel property = PropertyModel.findById(propertyId);
        // проверяем, не заполнено ли уже это свойство
        FileModel fileModel = FileModel.findById(fileId);
        FilePropertyModel checkProperty = FilePropertyModel.isPropertyExist(fileId, propertyId);
        if (checkProperty != null) {
            id = checkProperty.getId();
        }
        // если это добавление нового свойства
        if (id == 0) {
            FilePropertyModel fileProperty = new FilePropertyModel(fileId, propertyId, value);
            try {
                if (fileProperty.add()) {
                    logger.info("Файлу " + fileModel.getTitle() + ", id=" + fileModel.getId() + " добавлено свойство " + property.getTitle() + " = " + value + "; служебный номер - " + activeUser.getEmployeeId());
                    return "redirect:/file-view?id="+fileId;
                } else {
                    attr.addFlashAttribute("errors", fileProperty.errors);
                    attr.addFlashAttribute("selectedProperty", propertyId);
                    return "redirect:/file-property-add?id="+fileId;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Ошибка сервера");
            }
        } else {
            // если это изменение значения свойства
            try {
                FilePropertyModel fileProperty = FilePropertyModel.findById(id);
                fileProperty.setValue(value);
                if (fileProperty.update()) {
                    logger.info("У файла "+fileModel.getTitle()+", id="+fileModel.getId()+" имзменено значение свойства "+property.getTitle()+" на "+value+"; служебный номер - "+activeUser.getEmployeeId());
                    return "redirect:/file-view?id="+fileId;
                } else {
                    attr.addFlashAttribute("errors", fileProperty.errors);
                    return "redirect:/file-property-edit?id="+id;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Ошибка сервера");
            }
        }
    }
}
