package ru.khamitova.TestSystemNaumen.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex,
                                 Model model,
                                 HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        model.addAttribute("status", 404);
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
