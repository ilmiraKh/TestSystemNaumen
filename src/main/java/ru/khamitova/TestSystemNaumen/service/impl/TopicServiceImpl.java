package ru.khamitova.TestSystemNaumen.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import ru.khamitova.TestSystemNaumen.repository.TopicRepository;
import ru.khamitova.TestSystemNaumen.service.TopicService;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }
    @Override
    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    @Override
    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("topic.notFound"));
    }

    @Override
    public Topic create(Topic topic) {
        if (topicRepository.existsByName(topic.getName())) {
            throw new EntityAlreadyExistsException("topic.exists");
        }
        return topicRepository.save(topic);
    }

    @Override
    public Topic update(Topic topic) {
        Topic existing = topicRepository.findById(topic.getId())
                .orElseThrow(() -> new EntityNotFoundException("topic.notFound"));

        boolean existsSameName = topicRepository.existsByName(topic.getName());

        if (existsSameName && !existing.getName().equals(topic.getName())) {
            throw new EntityAlreadyExistsException("topic.exists");
        }

        existing.setName(topic.getName());
        existing.setDescription(topic.getDescription());

        return topicRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("topic.notFound"));

        topicRepository.delete(topic);
    }
}
