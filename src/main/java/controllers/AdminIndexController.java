package controllers;

import db.Database2;
import models.CategoryModel;
import models.FileModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AdminIndexController {

    @RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
    public String index(@RequestParam(value="name", required=false) String name, Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        System.out.println(currentPrincipalName);
        Object s = authentication.getDetails();

        System.out.println(principal.getName());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("User has authorities: " + userDetails.getAuthorities());

        model.addAttribute("pageTitle", "Index");

        int fileCount = FileModel.getCount();
        model.addAttribute("fileCount", fileCount);

        int categoryCount = CategoryModel.getCount();
        model.addAttribute("categoryCount", categoryCount);

        return "admin/index";
    }
}
