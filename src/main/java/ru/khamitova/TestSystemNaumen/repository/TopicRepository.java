package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
    Page<Topic> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
