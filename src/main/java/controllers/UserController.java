package controllers;

import models.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class UserController {


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String index(Model model) {
        ArrayList<HashMap> users = null;
        try {
            users = UserModel.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Users");
        //returns the view name
        return "user/users";
    }

    @RequestMapping(value = "/user-edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id", required=true) int id, Model model) {
        UserModel user = null;
        try {
            user = UserModel.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit user");
        //returns the view name
        return "user/user-edit";

    }

}
