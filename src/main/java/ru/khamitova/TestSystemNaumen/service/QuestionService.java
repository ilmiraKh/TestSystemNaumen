package ru.khamitova.TestSystemNaumen.service;

import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;

import java.util.List;

public interface QuestionService {
    List<Question> findAllByTest(Test test);
    Question findByIdAndUser(Long id, User user);
    Question create(Question question, User user);
    Question update(Question question, User user);
    void deleteByIdAndUser(Long id, User user);
}
