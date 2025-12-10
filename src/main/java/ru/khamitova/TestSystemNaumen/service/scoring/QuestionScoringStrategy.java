package ru.khamitova.TestSystemNaumen.service.scoring;

import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;

public interface QuestionScoringStrategy {
    QuestionType getSupportedType();
    double score(Question question, String[] rawValues, Answer answer);
}

