package ru.khamitova.TestSystemNaumen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {
    private Long id;
    private String title;
    private String description;
    private Long topicId;
    private String topicName;
}
