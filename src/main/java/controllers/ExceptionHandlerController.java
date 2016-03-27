package controllers;

import exceptions.ForbiddenException;
import exceptions.InternalException;
import exceptions.NotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
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
