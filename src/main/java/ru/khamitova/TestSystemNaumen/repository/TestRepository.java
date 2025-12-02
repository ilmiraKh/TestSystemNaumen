package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findAllByUser(User user);
    Optional<Test> findByIdAndUser(Long id, User user);

    @Query("""
    SELECT t FROM Test t
    WHERE t.published = true
      AND (:topicId IS NULL OR t.topic.id = :topicId)
      AND (:topicName IS NULL OR LOWER(t.topic.name) LIKE LOWER(CONCAT('%', :topicName, '%')))
      AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
    ORDER BY t.title
    """)
    Page<Test> findByFilters(
            @Param("topicId") Long topicId,
            @Param("topicName") String topicName,
            @Param("search") String search,
            Pageable pageable
    );

}
