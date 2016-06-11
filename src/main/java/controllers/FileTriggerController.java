package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.FileModel;
import models.FilePropertyModel;
import models.FileTriggerModel;
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
public class FileTriggerController {
    private static final Logger logger = Logger.getLogger(FileTriggerController.class);

    @RequestMapping(value = {"/file-triggers" }, method = RequestMethod.GET)
    public String fileTriggers(
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка просмотра списка триггеров (/file-triggers) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            ArrayList<FileTriggerModel> triggers = FileTriggerModel.findAll();
            model.addAttribute("triggers", triggers);
        } catch (SQLException e) {
            throw new NotFoundException("Страница не найдена");
        }

        model.addAttribute("pageTitle", "Триггеры");
        return "file-trigger/file-triggers";
    }

    @RequestMapping(value = {"/file-trigger-add" }, method = RequestMethod.GET)
    public String fileTriggerAdd(
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка создания триггера (/file-trigger-add) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        model.addAttribute("pageTitle", "Создать триггер");
        return "file-trigger/file-trigger-add";
    }

    @RequestMapping(value = {"/file-trigger-edit" }, method = RequestMethod.GET)
    public String fileTriggerEdit(
            @RequestParam("id") int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка изменения триггера (/file-trigger-edit) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            FileTriggerModel trigger = FileTriggerModel.findById(id);
            model.addAttribute("trigger", trigger);
            model.addAttribute("pageTitle", "Изменить триггер");
            return "file-trigger/file-trigger-edit";
        } catch (SQLException e) {
            throw new NotFoundException("Триггер не найден");
        }
    }

    @RequestMapping(value = {"/file-trigger-handler" }, method = RequestMethod.POST)
    public String addHandler(
            @RequestParam("extension") String extension,
            @RequestParam("command") String command,
            @RequestParam("regexp") String regexp,
            @RequestParam(value="id", required=false, defaultValue = "0") int id,
            RedirectAttributes attr,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка добавления/изменения триггера (/file-trigger-handler) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        FileTriggerModel trigger;
        // если это изменение свойства
        if (id > 0) {
            try {
                trigger = FileTriggerModel.findById(id);
                trigger.setExtension(extension);
                trigger.setCommand(command);
                trigger.setRegexp(regexp);
                if (trigger.update()) {
                    logger.info("Триггер изменен, id=" + trigger.getId()+"; служебный номер - "+activeUser.getEmployeeId());
                    return "redirect:/file-triggers";
                } else {
                    attr.addFlashAttribute("errors", trigger.errors);
                    return "redirect:/file-trigger-edit?id="+id;
                }
            } catch (SQLException e) {
                throw new NotFoundException("Триггер не найдено", "404");
            }
        } else {
            // если это добавление нового свойства
            trigger = new FileTriggerModel();
            trigger.setExtension(extension);
            trigger.setCommand(command);
            trigger.setRegexp(regexp);
            try {
                if (trigger.add()) {
                    logger.info("Добавлен новый триггер, id=" + trigger.getId()+"; служебный номер - "+activeUser.getEmployeeId());
                    return "redirect:/file-triggers";
                } else {
                    attr.addFlashAttribute("errors", trigger.errors);
                    attr.addFlashAttribute("trigger", trigger);
                    return "redirect:/file-trigger-add";
                }
            } catch (SQLException e) {
                throw new NotFoundException("Ошибка при добавлении триггера", "500");
            }
        }
    }

    @RequestMapping(value = {"/file-trigger-delete" }, method = RequestMethod.POST)
    public @ResponseBody String fileTriggerDelete(
            @RequestParam("id") int id,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка удаления триггера (/file-trigger-delete) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        JSONObject result = new JSONObject();
        boolean error = true;
        try {
            FileTriggerModel trigger = FileTriggerModel.findById(id);
            if (trigger.delete()) {
                error = false;
                logger.info("Триггер id="+id+" (/file-trigger-delete) удален; служебный номер - "+activeUser.getEmployeeId());
            }
        } catch (SQLException e) {
            throw new NotFoundException("Страница не найдена");
        }

        result.put("error", error);
        return result.toJSONString();
    }
}
