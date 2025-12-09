package ru.khamitova.TestSystemNaumen.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khamitova.TestSystemNaumen.entity.Option;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;
import ru.khamitova.TestSystemNaumen.repository.QuestionRepository;
import ru.khamitova.TestSystemNaumen.service.QuestionService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<Question> findAllByTest(Test test) {
        return questionRepository.findAllByTest(test)
                .stream()
                .sorted(Comparator.comparing(Question::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public Question findByIdAndUser(Long id, User user) {
        return questionRepository.findById(id)
                .filter(q -> q.getTest().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException("question.notFound"));
    }

    @Override
    @Transactional
    public Question create(Question question, User user) {
        if (!question.getTest().getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("test.notFound");
        }

        checkTestEditable(question);

        manualCheckRequired(question);

        validateOptions(question);
        question.getOptions().forEach(option -> {
            option.setQuestion(question);
            if (option.getIsCorrect() == null) option.setIsCorrect(false);
        });

        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question update(Question question, User user) {
        Question existing = questionRepository.findById(question.getId())
                .orElseThrow(() -> new EntityNotFoundException("question.notFound"));

        if (!existing.getTest().getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("question.notFound");
        }

        checkTestEditable(existing);

        existing.setText(question.getText());
        existing.setType(question.getType());
        existing.setPoints(question.getPoints());

        if (existing.getType() == QuestionType.OPEN) {
            existing.setCorrectAnswer(question.getCorrectAnswer());
            manualCheckRequired(existing);

            existing.getOptions().clear();
        } else if (existing.getType() == QuestionType.CHOICE) {
            existing.setCorrectAnswer(null);

            validateOptions(question);
            List<Option> validOptions = question.getOptions();
            validOptions.forEach(option -> {
                option.setQuestion(existing);
                if (option.getIsCorrect() == null) option.setIsCorrect(false);
            });

            existing.getOptions().clear();
            existing.getOptions().addAll(validOptions);
        }

        return questionRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteByIdAndUser(Long id, User user) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("question.notFound"));

        if (!question.getTest().getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("question.notFound");
        }

        if (question.getTest().getPublished()) {
            throw new IllegalStateException("question.cannotEditPublishedTest");
        }

        questionRepository.delete(question);
    }


    private void manualCheckRequired(Question question) {
        question.setManualCheckRequired(false);

        if (question.getType() != QuestionType.OPEN) {
            return;
        }

        String answer = question.getCorrectAnswer();

        if (answer == null || answer.trim().isEmpty()) {
            question.setManualCheckRequired(true);
        }
    }

    private void validateOptions(Question question) {
        if (question.getType() == QuestionType.CHOICE) {
            List<Option> options = question.getOptions();
            if (options == null || options.isEmpty()) {
                throw new IllegalArgumentException("question.atLeastOneOptionRequired");
            }

            List<Option> validOptions = options.stream()
                    .filter(o -> o != null && o.getText() != null && !o.getText().trim().isEmpty())
                    .toList();

            if (validOptions.isEmpty()) {
                throw new IllegalArgumentException("question.atLeastOneOptionRequired");
            }

            boolean hasCorrect = validOptions.stream().anyMatch(o -> Boolean.TRUE.equals(o.getIsCorrect()));

            if (!hasCorrect) {
                throw new IllegalArgumentException("question.atLeastOneCorrectRequired");
            }

            question.setOptions(validOptions);
        }
    }

    private void checkTestEditable(Question question) {
        if (question.getTest().getPublished()) {
            throw new IllegalStateException("question.cannotEditPublishedTest");
        }
    }
}
