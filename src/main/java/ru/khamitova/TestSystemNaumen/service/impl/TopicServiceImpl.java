package ru.khamitova.TestSystemNaumen.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import ru.khamitova.TestSystemNaumen.repository.TopicRepository;
import ru.khamitova.TestSystemNaumen.service.TopicService;

@Service
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public Page<Topic> getPageByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name"));

        if (name == null || name.isBlank()) {
            return topicRepository.findAll(pageable);
        }

        return topicRepository.findByNameContainingIgnoreCase(name, pageable);
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
    @Transactional
    public void deleteById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("topic.notFound"));

        topicRepository.delete(topic);
    }
}
