package ru.khamitova.TestSystemNaumen.exception;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntityAlreadyExistsException extends RuntimeException {
    private Long existingId;
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
    public EntityAlreadyExistsException(String message, Long id) {
        super(message);
        existingId = id;
    }
}

