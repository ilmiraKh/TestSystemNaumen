package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByIdAndTest_User(Long resultId, User teacher);
    List<Result> findAllByTestAndTest_User(Test test, User teacher);
    List<Result> findAllByTestAndTest_UserAndStatus(Test test, User teacher, ResultStatus status);
    Optional<Result> findByTestAndUser(Test test, User user);
    Optional<Result> findByIdAndUser(Long id, User user);
}
