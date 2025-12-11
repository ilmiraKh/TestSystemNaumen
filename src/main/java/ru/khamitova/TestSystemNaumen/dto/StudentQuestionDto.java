package ru.khamitova.TestSystemNaumen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class StudentQuestionDto {
    private Long id;
    private String text;
    private Map<Long, String> options;
}
