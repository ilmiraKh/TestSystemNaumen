package ru.khamitova.TestSystemNaumen.service;

import org.springframework.data.domain.Page;
import ru.khamitova.TestSystemNaumen.entity.Topic;

public interface TopicService {
    Page<Topic> getPageByName(String name, int page, int size);
    Topic findById(Long idr);
    Topic create(Topic topic);
    Topic update(Topic topic);
    void deleteById(Long id);
}
