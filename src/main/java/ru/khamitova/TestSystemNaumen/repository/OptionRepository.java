package ru.khamitova.TestSystemNaumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.khamitova.TestSystemNaumen.entity.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
