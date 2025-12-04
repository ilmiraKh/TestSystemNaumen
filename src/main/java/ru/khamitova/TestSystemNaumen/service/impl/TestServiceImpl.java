package ru.khamitova.TestSystemNaumen.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import jakarta.persistence.EntityNotFoundException;
import ru.khamitova.TestSystemNaumen.repository.TestRepository;
import ru.khamitova.TestSystemNaumen.service.TestService;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public List<Test> findAllByUser(User user) {
        return testRepository.findAllByUser(user);
    }

    @Override
    public Test findByIdAndUser(Long id, User user) {
        return testRepository.findByIdAndUser(id, user).orElseThrow(()-> new EntityNotFoundException("test.notFound"));
    }

    @Override
    public Test findById(Long id) {
        Test test = testRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("test.notFound"));
        if (!test.getPublished()) throw new EntityNotFoundException("test.notFound");
        return test;
    }

    @Override
    public Test create(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Test update(Test test, User user) {
        Test existing = testRepository.findById(test.getId())
                .orElseThrow(() -> new EntityNotFoundException("test.notFound"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("test.notFound");
        }

        existing.setTitle(test.getTitle());
        existing.setDescription(test.getDescription());
        existing.setPublished(test.getPublished());

        return testRepository.save(existing);
    }

    @Override
    public void deleteByIdAndUser(Long id, User user) {
        Test test = testRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("test.notFound"));

        testRepository.delete(test);
    }

    @Override
    public Page<Test> getPageByFilters(String search, Long topicId, String topicName, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("title"));
        return testRepository.findByFilters(topicId, topicName, search, pageable);
    }
}
