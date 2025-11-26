package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
