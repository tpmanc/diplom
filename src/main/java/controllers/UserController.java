package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.UserModel;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class);

    @RequestMapping(value = {"/users" }, method = RequestMethod.GET)
    public String users(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка просмотра пользователей (/users) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        int limit = UserModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        int pageCount = (int) Math.ceil((float)UserModel.getCount() / limit);
        model.addAttribute("pageCount", pageCount);

        try {
            ArrayList<UserModel> users = UserModel.findAll(limit, offset);
            model.addAttribute("users", users);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Пользователи");
        return "user/users";
    }

    @RequestMapping(value = {"/user-view" }, method = RequestMethod.GET)
    public String viewUser(
            @RequestParam int id,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка просмотра пользователя (/user-view) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        UserModel user = UserModel.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Пользователи");
        return "user/user-view";
    }
}
