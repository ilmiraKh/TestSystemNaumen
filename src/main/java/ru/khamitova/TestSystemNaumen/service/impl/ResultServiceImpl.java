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
import ru.khamitova.TestSystemNaumen.service.scoring.QuestionScoringStrategy;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements ResultService {
    private final ResultRepository resultRepository;
    private final Map<QuestionType, QuestionScoringStrategy> scoringStrategies;
    private final ResultUtil resultUtil;

    @Autowired
    public ResultServiceImpl(ResultRepository resultRepository,
                             List<QuestionScoringStrategy> strategies,
                             ResultUtil resultUtil) {
        this.resultRepository = resultRepository;
        this.scoringStrategies = strategies.stream()
                .collect(Collectors.toMap(QuestionScoringStrategy::getSupportedType, s -> s));
        this.resultUtil = resultUtil;
    }

    @Override
    public Result findByIdAndUser(Long id, User student) {
        Result result = resultRepository.findByIdAndUser(id, student).orElseThrow(() -> new EntityNotFoundException("result.notFound"));
        resultUtil.selectOptions(result);
        return result;
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

            QuestionScoringStrategy scoringStrategy = scoringStrategies.get(question.getType());
            if (scoringStrategy == null) {
                throw new IllegalStateException("question.unsupportedType");
            }

            double earnedPoints = scoringStrategy.score(question, rawValues, answer);
            if (!question.getManualCheckRequired()) {
                totalScore += earnedPoints;
            }

            answerList.add(answer);
        }

        result.setAnswers(answerList);
        result.setScore(totalScore);
        result.setStatus(requiresManualCheck ? ResultStatus.WAITING : ResultStatus.CHECKED);

        return resultRepository.save(result);
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

