package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findAllByUser(User user);
    Optional<Test> findByIdAndUser(Long id, User user);
}
