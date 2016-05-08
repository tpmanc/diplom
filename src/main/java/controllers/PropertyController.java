package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.PropertyModel;
import org.apache.log4j.Logger;
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
 * Контроллер свойств для администратора
 */
@Controller
public class PropertyController {
    private static final Logger logger = Logger.getLogger(PropertyController.class);

    /**
     * Список всех свойств
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/properties" }, method = RequestMethod.GET)
    public String index(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка просмотра списка свойств (/properties) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        int limit = PropertyModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        try {
            ArrayList<PropertyModel> properties = PropertyModel.findAll(limit, offset);
            model.addAttribute("properties", properties);

            int pageCount = (int) Math.ceil((float)PropertyModel.getCount() / limit);
            model.addAttribute("pageCount", pageCount);

            model.addAttribute("page", page);
            model.addAttribute("pageTitle", "Свойства файлов");
            return "property/properties";
        } catch (SQLException e) {
            throw new NotFoundException("Страница не найдена");
        }
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
            logger.warn("Попытка добавления свойства (/property-add) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
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
            logger.warn("Попытка изменения свойства (/property-edit) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
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
            logger.warn("Попытка просмотра свойства (/property-view) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
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
            logger.warn("Попытка добавления/изменения свойства (/property-handler) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
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
                    logger.info("Название свойства было изменено с "+oldTitle+"на "+property.getTitle()+", id=" + property.getId()+"; служебный номер - "+activeUser.getEmployeeId());
                    return "redirect:/properties";
                } else {
                    attr.addFlashAttribute("errors", property.errors);
                    return "redirect:/property-edit?id="+id;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Свойство не найдено", "404");
            }
        } else {
            // если это добавление нового свойства
            property = new PropertyModel(title);
            try {
                if (property.add()) {
                    logger.info("Добавлено новое свойство "+property.getTitle()+", id=" + property.getId()+"; служебный номер - "+activeUser.getEmployeeId());
                    return "redirect:/properties";
                } else {
                    attr.addFlashAttribute("errors", property.errors);
                    return "redirect:/property-add";
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
            logger.warn("Попытка удаления свойства (/property-delete) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        // TODO: обработка удаления свойства
        return false;
    }

}
