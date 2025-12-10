package ru.khamitova.TestSystemNaumen.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.repository.ResultRepository;
import ru.khamitova.TestSystemNaumen.service.TeacherResultService;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.util.List;
import java.util.Map;

@Service
public class TeacherResultServiceImpl implements TeacherResultService {
    private final ResultRepository resultRepository;
    private final ResultUtil resultUtil;

    @Autowired
    public TeacherResultServiceImpl(ResultRepository resultRepository, ResultUtil resultUtil) {
        this.resultRepository = resultRepository;
        this.resultUtil = resultUtil;
    }

    @Override
    public Result getResultForTeacher(Long resultId, User teacher) {
        Result result = resultRepository.findByIdAndTest_User(resultId, teacher)
                .orElseThrow(() -> new EntityNotFoundException("result.notFound"));
        resultUtil.selectOptions(result);
        return result;
    }

    @Override
    @Transactional
    public void manualCheck(Long id, Map<Long, Double> points) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("result.notFound"));

        double totalScore = result.getScore() != null ? result.getScore() : 0.0;

        for (Answer answer : result.getAnswers()) {
            if (answer.getQuestion().getManualCheckRequired()) {
                Double awardedPoints = points.get(answer.getId());
                if (awardedPoints != null) {
                    answer.setPointsAwarded(awardedPoints);
                    totalScore += awardedPoints;
                }
            }
        }

        result.setScore(totalScore);
        result.setStatus(ResultStatus.CHECKED);
        resultRepository.save(result);
    }

    @Override
    public List<Result> findAllByTestAndUser(Test test, User teacher) {
        return resultRepository.findAllByTestAndTest_User(test, teacher);
    }

    @Override
    public List<Result> findAllByTestAndUserAndStatus(Test test, User teacher, ResultStatus status) {
        return resultRepository.findAllByTestAndTest_UserAndStatus(test, teacher, status);
    }
}
