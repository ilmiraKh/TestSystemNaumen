package ru.khamitova.TestSystemNaumen.dto;

import lombok.Getter;
import lombok.Setter;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;

import java.util.List;

@Getter
@Setter
public class ResultDto {
    private Long id;
    private Long testId;
    private ResultStatus status;
    private Double score;
    private List<AnswerDto> answers;
}
