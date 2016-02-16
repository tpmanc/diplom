package controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LogoutController {

    //@RequestMapping(value="/logout.html", method = RequestMethod.GET)
    public String hello() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null){
//            new SecurityContextLogoutHandler().logout(HttpSer);
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
        return "redirect:/login";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
    }
}
