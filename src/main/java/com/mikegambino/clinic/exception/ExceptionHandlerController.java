package com.mikegambino.clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("code", 404);
        model.addAttribute("description", ex.getMessage());
        return "error_page";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String forbidden(AccessDeniedException ex, Model model) {
        model.addAttribute("code", 404);
        return "error_page";
    }

    @ExceptionHandler(BadRequestException.class)
    public String badRequest(BadRequestException ex, Model model) {
        model.addAttribute("code", 400);
        model.addAttribute("description", ex.getMessage());
        return "error_page";
    }
}
