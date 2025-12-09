package ru.khamitova.TestSystemNaumen.service;

import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResultService {
    Result create(User student, Test test, Map<String, String[]> answers);
    Result findByIdAndUser(Long id, User student);
    Optional<Result> checkExistingResult(Test test, User user);
    Result getResultForTeacher(Long resultId, User teacher);
    List<Result> findAllByTestAndUserAndStatus(Test test, User teacher, ResultStatus status);
    List<Result> findAllByTestAndUser(Test test, User teacher);
    void manualCheck(Long id, Map<Long, Double> points);
}
