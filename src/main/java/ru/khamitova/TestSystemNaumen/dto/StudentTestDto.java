package ru.khamitova.TestSystemNaumen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class StudentTestDto {
    private Long id;
    private String title;
    private String description;
    private Long topicId;
    private String topicName;
    private List<StudentQuestionDto> questions;
}

