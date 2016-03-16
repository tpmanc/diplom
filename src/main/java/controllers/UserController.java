package controllers;

import auth.CustomUserDetails;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.FileModel;
import models.FileVersionModel;
import models.UserModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class UserController {
    @RequestMapping(value = {"/users" }, method = RequestMethod.GET)
    public String users(@RequestParam(value="page", required=false, defaultValue = "1") int page,
                                Model model,
                                Principal principal) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            throw new AccessDeniedException("Доступ запрещен");
        }

        int limit = UserModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        int pageCount = (int) Math.ceil((float)FileVersionModel.getUnfilledCount(activeUser.getEmployeeId()) / limit);
        model.addAttribute("pageCount", pageCount);

        try {
            ArrayList<HashMap> users = UserModel.findAll(limit, offset);
            model.addAttribute("users", users);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Пользователи");
        return "user/users";
    }

    @RequestMapping(value = {"/user-view" }, method = RequestMethod.GET)
    public String viewUser(@RequestParam int id,
                                Model model,
                                Principal principal) {
        try {
            UserModel user = UserModel.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Пользователи");
            return "user/user-view";
        } catch (SQLException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
