package controllers;

import exceptions.CustomSQLException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlingFilter {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(@RequestParam(value="e", required=true) Exception e, Model model) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        // Otherwise setup and send the user to a default error-view.
        model.addAttribute("code", e.getCause());
        model.addAttribute("codeMessage", e.getMessage());
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(CustomSQLException.class)
    public String handleIOException(@RequestParam(value="e", required=true) CustomSQLException e, Model model) {
        model.addAttribute("code", e.getCode());
        model.addAttribute("codeMessage", e.getMessage());
        return DEFAULT_ERROR_VIEW;
    }
}
