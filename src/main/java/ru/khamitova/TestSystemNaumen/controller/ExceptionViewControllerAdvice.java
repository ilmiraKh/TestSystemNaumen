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
public class ExceptionViewControllerAdvice {
    private final MessageSource messageSource;

    @Autowired
    public ExceptionViewControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String buildErrorResponse(Exception ex, Model model, HttpServletResponse response, Locale locale, int status) {
        String resolvedMessage = messageSource.getMessage(
                ex.getMessage(),
                new Object[]{},
                ex.getMessage(),
                locale
        );

        response.setStatus(status);
        model.addAttribute("status", status);
        model.addAttribute("error", resolvedMessage);
        return "error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex,
                                 Model model,
                                 HttpServletResponse response,
                                 Locale locale) {
        return buildErrorResponse(ex, model, response, locale, HttpServletResponse.SC_NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex,
                                     Model model,
                                     HttpServletResponse response,
                                     Locale locale) {
        return buildErrorResponse(ex, model, response, locale, HttpServletResponse.SC_BAD_REQUEST);
    }

}
