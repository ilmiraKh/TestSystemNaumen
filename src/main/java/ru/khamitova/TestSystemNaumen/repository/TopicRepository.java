package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
}
