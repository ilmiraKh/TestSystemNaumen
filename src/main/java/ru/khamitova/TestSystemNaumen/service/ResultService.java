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
    List<Result> findAllByUserAndStatus(User student, ResultStatus status);
    List<Result> findAllByUser(User student);
    Optional<Result> checkExistingResult(Test test, User user);
}
