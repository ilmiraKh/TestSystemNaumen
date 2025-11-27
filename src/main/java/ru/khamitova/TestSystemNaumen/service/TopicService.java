package ru.khamitova.TestSystemNaumen.service;

import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.entity.User;

import java.util.List;

public interface TopicService {
    List<Topic> getAll();
    Topic findById(Long idr);
    Topic create(Topic topic);
    Topic update(Topic topic);
    void deleteById(Long id);
}
