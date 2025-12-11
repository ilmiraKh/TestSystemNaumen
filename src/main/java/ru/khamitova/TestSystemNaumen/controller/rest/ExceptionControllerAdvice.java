package ru.khamitova.TestSystemNaumen.controller.rest;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.exception.InvalidRoleException;

import java.util.Locale;
import java.util.Map;

@RestControllerAdvice(basePackages = "ru.khamitova.TestSystemNaumen.controller.rest")
public class ExceptionControllerAdvice {

    private final MessageSource messageSource;

    @Autowired
    public ExceptionControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private ResponseEntity<Map<String, String>> buildResponse(Exception ex, Locale locale, HttpStatus status) {
        String resolvedMessage = messageSource.getMessage(
                ex.getMessage(),
                new Object[]{},
                ex.getMessage(),
                locale
        );
        return ResponseEntity.status(status).body(Map.of("error", resolvedMessage));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex, Locale locale) {
        return buildResponse(ex, locale, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex, Locale locale) {
        return buildResponse(ex, locale, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExists(EntityAlreadyExistsException ex, Locale locale) {
        return buildResponse(ex, locale, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRole(InvalidRoleException ex, Locale locale) {
        return buildResponse(ex, locale, HttpStatus.BAD_REQUEST);
    }
}
