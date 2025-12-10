package ru.khamitova.TestSystemNaumen.service.scoring;

import org.springframework.stereotype.Component;
import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Option;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChoiceQuestionScoringStrategy implements QuestionScoringStrategy {
    @Override
    public QuestionType getSupportedType() {
        return QuestionType.CHOICE;
    }

    @Override
    public double score(Question question, String[] rawValues, Answer answer) {
        Set<Long> selectedOptionIds = Arrays.stream(rawValues)
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        answer.setAnswer(String.join(",", rawValues));
        answer.setSelectedOptionIds(selectedOptionIds);

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
}

