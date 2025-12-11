package ru.khamitova.TestSystemNaumen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnswerDto {
    private Long id;
    private Long questionId;
    private String questionText;
    private String answer;
    private Double pointsAwarded;
}
