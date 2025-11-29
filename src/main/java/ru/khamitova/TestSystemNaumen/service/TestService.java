package ru.khamitova.TestSystemNaumen.service;

import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;

import java.util.List;

public interface TestService {
    List<Test> findAllByUser(User user);
    Test findByIdAndUser(Long id, User user);
    Test create(Test test);
    Test update(Test test, User user);
    void deleteByIdAndUser(Long id, User user);
}
