package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.LogModel;
import models.PropertyModel;
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
import java.util.HashMap;

/**
 * Контроллер свойств для администратора
 */
@Controller
public class PropertyController {

    /**
     * Список всех свойств
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/properties" }, method = RequestMethod.GET)
    public String index(
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка просмотра списка свойств (/properties) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        ArrayList<PropertyModel> properties = null;
        try {
            properties = PropertyModel.findAll();
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Свойства файлов");
        return "property/properties";
    }

    /**
     * Добавление свойства
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/property-add" }, method = RequestMethod.GET)
    public String add(
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка добавления свойства (/property-add) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        model.addAttribute("pageTitle", "Добавить свойство файла");
        return "property/property-add";
    }

    /**
     * Изменение свойства
     * @param id Id свойства
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/property-edit" }, method = RequestMethod.GET)
    public String update(
            @RequestParam("id") int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка изменения свойства (/property-edit) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        try {
            PropertyModel property = PropertyModel.findCustomById(id);
            model.addAttribute("property", property);
            model.addAttribute("pageTitle", "Изменить свойство файла");
            return "property/property-edit";
        } catch (SQLException e) {
            throw new NotFoundException("Свойство не найдено");
        }
    }

    /**
     * Просмотр свойства
     * @param id Id свойства
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/property-view" }, method = RequestMethod.GET)
    public String view(
            @RequestParam("id") int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка просмотра свойства (/property-view) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }

        PropertyModel property = PropertyModel.findById(id);
        model.addAttribute("property", property);

        model.addAttribute("pageTitle", "Просмотр свойства");
        return "property/property-view";
    }

    /**
     * Обработчик создания и изменения свойства
     * @param title Название свойства
     * @param id Id свойства
     * @param attr
     * @return Перенаправление
     */
    @RequestMapping(value = {"/property-handler" }, method = RequestMethod.POST)
    public String addHandler(
            @RequestParam("title") String title,
            @RequestParam(value="id", required=false, defaultValue = "0") int id,
            RedirectAttributes attr,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка добавления/изменения свойства (/property-handler) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }

        PropertyModel property;
        // если это изменение свойства
        if (id > 0) {
            try {
                property = PropertyModel.findCustomById(id);
                String oldTitle = property.getTitle();
                property.setTitle(title);
                if (property.update()) {
                    LogModel.addInfo(activeUser.getEmployeeId(), "Название свойства было изменено с "+oldTitle+"на "+property.getTitle()+", id=" + property.getId());
                    return "redirect:/admin/properties";
                } else {
                    attr.addFlashAttribute("errors", property.errors);
                    return "redirect:/admin/property-edit?id="+id;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Свойство не найдено", "404");
            }
        } else {
            // если это добавление нового свойства
            property = new PropertyModel(title);
            try {
                if (property.add()) {
                    LogModel.addInfo(activeUser.getEmployeeId(), "Добавлено новое свойство "+property.getTitle()+", id=" + property.getId());
                    return "redirect:/admin/properties";
                } else {
                    attr.addFlashAttribute("errors", property.errors);
                    return "redirect:/admin/property-add";
                }
            } catch (SQLException e) {
                throw new NotFoundException("Ошибка при добавлении свойства", "500");
            }
        }
    }

    /**
     * Обработчик ajax запроса на удаление свойства
     * @param id Id свойства
     * @param model
     * @return json строка
     */
    @RequestMapping(value = {"/property-delete" }, method = RequestMethod.POST)
    public @ResponseBody boolean deleteHandler(
            @RequestParam("id") int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка удаления свойства (/property-delete) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        // TODO: обработка удаления свойства
        return false;
    }

}
