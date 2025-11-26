package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}
