package ru.khamitova.TestSystemNaumen.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class ExceptionControllerAdvice {
    private final MessageSource messageSource;

    @Autowired
    public ExceptionControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex,
                                 Model model,
                                 HttpServletResponse response,
                                 Locale locale) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        String resolvedMessage = messageSource.getMessage(
                ex.getMessage(),
                new Object[]{},
                ex.getMessage(),
                locale
        );

        model.addAttribute("status", 404);
        model.addAttribute("error", resolvedMessage);
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex,
                                     Model model,
                                     HttpServletResponse response,
                                     Locale locale) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String resolvedMessage = messageSource.getMessage(
                ex.getMessage(),
                new Object[]{},
                ex.getMessage(),
                locale
        );

        model.addAttribute("status", 400);
        model.addAttribute("error", resolvedMessage);
        return "error";
    }

}
