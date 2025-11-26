package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
