package ru.khamitova.TestSystemNaumen.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("user.exists");
    }
}

