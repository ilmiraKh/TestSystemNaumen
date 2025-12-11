package ru.khamitova.TestSystemNaumen.mapper;

import org.springframework.stereotype.Component;
import ru.khamitova.TestSystemNaumen.dto.AnswerDto;
import ru.khamitova.TestSystemNaumen.dto.ResultDto;
import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ResultMapper {

    public AnswerDto toAnswerDto(Answer answer) {
        AnswerDto dto = new AnswerDto();
        dto.setId(answer.getId());
        dto.setAnswer(answer.getAnswer());
        dto.setPointsAwarded(answer.getPointsAwarded());

        if (answer.getQuestion() != null) {
            dto.setQuestionId(answer.getQuestion().getId());
            dto.setQuestionText(answer.getQuestion().getText());
        }

        return dto;
    }

    public ResultDto toDto(Result result) {
        ResultDto dto = new ResultDto();
        dto.setId(result.getId());
        dto.setStatus(result.getStatus());

        if (result.getStatus() == ResultStatus.WAITING){
            dto.setAnswers(Collections.emptyList());
        } else {
            dto.setScore(result.getScore());

            dto.setAnswers(result.getAnswers().stream()
                    .map(this::toAnswerDto)
                    .collect(Collectors.toList())
            );
        }

        if (result.getTest() != null) {
            dto.setTestId(result.getTest().getId());
        }

        return dto;
    }

    public ResultDto toTeacherCheckDto(Result result) {
        ResultDto dto = new ResultDto();
        dto.setId(result.getId());
        dto.setStatus(result.getStatus());
        dto.setScore(result.getScore());

        dto.setAnswers(result.getAnswers().stream()
                .map(this::toAnswerDto)
                .collect(Collectors.toList())
        );

        if (result.getTest() != null) {
            dto.setTestId(result.getTest().getId());
        }

        return dto;
    }
}

