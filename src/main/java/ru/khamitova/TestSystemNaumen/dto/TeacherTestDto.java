package ru.khamitova.TestSystemNaumen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherTestDto {
    private Long id;

    @NotBlank(message = "{test.title.notBlank}")
    @Size(min = 3, max = 255, message = "{test.title.size}")
    private String title;

    @Size(max = 5000, message = "{test.description.size}")
    private String description;

    private Long topicId;

    private Boolean published;
}
