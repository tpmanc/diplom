package controllers;

import exceptions.ForbiddenException;
import exceptions.InternalException;
import exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorController {
    @RequestMapping(value = {"/forbidden" }, method = RequestMethod.GET)
    public String forbidden() {
        return "error403";
    }

    @RequestMapping(value = {"/not-found" }, method = RequestMethod.GET)
    public String notFound() {
        return "error404";
    }

    @RequestMapping(value = {"/internal-error" }, method = RequestMethod.GET)
    public String internalError() {
        return "error500";
    }

    @ExceptionHandler(NotFoundException.class)
    public String notFoundException(Exception exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error404";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String forbiddenException(Exception exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error403";
    }

    @ExceptionHandler(InternalException.class)
    public String internalException(Exception exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error500";
    }
}
