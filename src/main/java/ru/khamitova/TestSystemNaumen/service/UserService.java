package ru.khamitova.TestSystemNaumen.service;

import ru.khamitova.TestSystemNaumen.dto.UserRegistrationDto;
import ru.khamitova.TestSystemNaumen.entity.User;

public interface UserService {
    void register(UserRegistrationDto dto);
    User findByEmail(String email);
}
