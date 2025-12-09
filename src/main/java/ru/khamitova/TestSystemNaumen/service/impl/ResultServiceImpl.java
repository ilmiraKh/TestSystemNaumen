package ru.khamitova.TestSystemNaumen.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khamitova.TestSystemNaumen.entity.*;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.repository.ResultRepository;
import ru.khamitova.TestSystemNaumen.service.ResultService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements ResultService {
    private final ResultRepository resultRepository;

    @Autowired
    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public Result findByIdAndUser(Long id, User student) {
        return resultRepository.findByIdAndUser(id, student).orElseThrow(() -> new EntityNotFoundException("result.notFound"));
    }

    @Override
    @Transactional
    public Result create(User student, Test test, Map<String, String[]> answers) {
        Optional<Result> existing = checkExistingResult(test, student);
        if (existing.isPresent()){
            throw new EntityAlreadyExistsException("result.exists", existing.get().getId());
        }
        Result result = new Result();
        result.setTest(test);
        result.setUser(student);

        List<Answer> answerList = new ArrayList<>();
        double totalScore = 0.0;
        boolean requiresManualCheck = false;

        for (Question question : test.getQuestions()) {
            if (question.getManualCheckRequired()) {
                requiresManualCheck = true;
            }

            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setResult(result);

            String key = "question_" + question.getId();
            String[] rawValues = answers.get(key);

            if (rawValues == null || rawValues.length == 0 || (question.getType() == QuestionType.OPEN && rawValues[0].isBlank())) {
                answer.setAnswer("");
                answer.setPointsAwarded(0.0);
                answerList.add(answer);
                continue;
            }

            switch (question.getType()) {
                case OPEN -> {
                    handleOpenQuestion(answer, question, rawValues[0]);
                    if (!question.getManualCheckRequired()) {
                        totalScore += answer.getPointsAwarded();
                    }
                }
                case CHOICE -> {
                    totalScore += handleChoiceQuestion(answer, question, rawValues);
                }
            }

            answerList.add(answer);
        }

        result.setAnswers(answerList);
        result.setScore(totalScore);
        result.setStatus(requiresManualCheck ? ResultStatus.WAITING : ResultStatus.CHECKED);

        return resultRepository.save(result);
    }

    private void handleOpenQuestion(Answer answer, Question question, String userAnswer) {
        answer.setAnswer(userAnswer);

        if (question.getManualCheckRequired()) {
            answer.setPointsAwarded(null);
            return;
        }

        boolean correct = question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
        answer.setPointsAwarded(correct ? question.getPoints().doubleValue() : 0.0);
    }

    private double handleChoiceQuestion(Answer answer, Question question, String[] rawValues) {
        Set<Long> selectedOptionIds = Arrays.stream(rawValues)
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        answer.setAnswer(String.join(",", rawValues));

        Set<Long> correctOptionIds = question.getOptions().stream()
                .filter(Option::getIsCorrect)
                .map(Option::getId)
                .collect(Collectors.toSet());

        long correctSelectedCount = selectedOptionIds.stream()
                .filter(correctOptionIds::contains)
                .count();

        double pointsPerCorrect = question.getPoints().doubleValue() / correctOptionIds.size();
        double earnedPoints = pointsPerCorrect * correctSelectedCount;

        answer.setPointsAwarded(earnedPoints);
        return earnedPoints;
    }

    @Override
    public Optional<Result> checkExistingResult(Test test, User user){
        return resultRepository.findByTestAndUser(test, user);
    }

    @Override
    public List<Result> findAllByUserAndStatus(User student, ResultStatus status) {
        return resultRepository.findAllByUserAndStatus(student, status);
    }

    @Override
    public List<Result> findAllByUser(User student) {
        return resultRepository.findAllByUser(student);
    }
}

