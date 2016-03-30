package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Контроллер категорий для модератора
 */
@Controller
public class LogController {

    /**
     * Вывод логов
     */
    @RequestMapping(value = {"/logs" }, method = RequestMethod.GET)
    public String index(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            @RequestParam(value="level", required=false) String level,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка просмотра логов (/logs) без прав администратора");
            throw new AccessDeniedException("Доступ запрещен");
        }
        try {
            int limit = LogModel.PAGE_COUNT;
            int offset = (page - 1) * limit;

            ArrayList<LogOutput> logs = LogModel.findAll(level, limit, offset);
            model.addAttribute("logs", logs);

            int pageCount;
            if (level == null) {
                pageCount = (int) Math.ceil((float) LogModel.getCount() / limit);
            } else {
                pageCount = (int) Math.ceil((float) LogModel.getCount(level) / limit);
            }
            model.addAttribute("pageCount", pageCount);

            model.addAttribute("page", page);
            model.addAttribute("currentLevel", level);
            model.addAttribute("pageTitle", "Логи");
            return "log/logs";
        } catch (SQLException e) {
            throw new NotFoundException("Станица не найдена");
        }
    }

    @RequestMapping(value = {"/logs-clear" }, method = RequestMethod.POST)
    public String logsClear(
            Principal principal,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка очистки логов (/logs-clear) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }
        LogModel.clear();
        LogModel.addInfo(activeUser.getEmployeeId(), "Очистка логов");
        attr.addFlashAttribute("status", true);
        return "redirect:/logs";
    }
}
