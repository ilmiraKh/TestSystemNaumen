package ru.khamitova.TestSystemNaumen.service.scoring;

import org.springframework.stereotype.Component;
import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;

@Component
public class OpenQuestionScoringStrategy implements QuestionScoringStrategy {
    @Override
    public QuestionType getSupportedType() {
        return QuestionType.OPEN;
    }

    @Override
    public double score(Question question, String[] rawValues, Answer answer) {
        String userAnswer = rawValues[0];
        answer.setAnswer(userAnswer);

        if (Boolean.TRUE.equals(question.getManualCheckRequired())) {
            answer.setPointsAwarded(null);
            return 0.0;
        }

        boolean correct = question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
        double points = correct ? question.getPoints().doubleValue() : 0.0;
        answer.setPointsAwarded(points);
        return points;
    }
}

