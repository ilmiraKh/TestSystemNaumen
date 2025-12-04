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
    @Query("SELECT r FROM Result r WHERE r.test.user.id = :teacherId AND r.status = :status")
    List<Result> findResultsForTeacherByStatus(@Param("teacherId") Long teacherId,
                                               @Param("status") ResultStatus status);

    Optional<Result> findByTestAndUser(Test test, User user);
    Optional<Result> findByIdAndUser(Long id, User user);
}
