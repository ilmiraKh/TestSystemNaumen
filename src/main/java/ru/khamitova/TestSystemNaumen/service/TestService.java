package ru.khamitova.TestSystemNaumen.service;

import org.springframework.data.domain.Page;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;

import java.util.List;

public interface TestService {
    List<Test> findAllByUser(User user);
    Test findByIdAndUser(Long id, User user);
    Test findById(Long id);
    Test create(Test test);
    Test update(Test test, User user);
    void deleteByIdAndUser(Long id, User user);
    Page<Test> getPageByFilters(String search, Long topicId, String topicName, int page, int size);
}
