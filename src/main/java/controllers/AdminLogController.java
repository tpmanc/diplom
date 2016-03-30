package controllers;

import auth.CustomUserDetails;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.CategoryModel;
import models.LogModel;
import models.UserModel;
import models.helpers.LogOutput;
import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Контроллер категорий для модератора
 */
@Controller
@RequestMapping("/admin")
public class AdminLogController {

    /**
     * Вывод логов
     */
    @RequestMapping(value = {"/logs" }, method = RequestMethod.GET)
    public String index(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка просмотра логов без прав администратора");
            throw new AccessDeniedException("Доступ запрещен");
        }
        try {
            int limit = LogModel.PAGE_COUNT;
            int offset = (page - 1) * limit;

            ArrayList<LogOutput> logs = LogModel.findAll(limit, offset);
            model.addAttribute("logs", logs);
            model.addAttribute("pageTitle", "Логи");
            return "log/logs";
        } catch (SQLException e) {
            throw new NotFoundException("Станица не найдена");
        }
    }
}
