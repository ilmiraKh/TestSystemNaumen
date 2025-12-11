package ru.khamitova.TestSystemNaumen.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private Long id;

    @NotBlank(message = "{question.text.required}")
    private String text;

    @NotNull(message = "{question.type.required}")
    private QuestionType type;

    @NotNull(message = "{question.points.required}")
    @Min(value = 1, message = "{question.points.positive}")
    private Integer points;

    @NotNull(message = "{question.manualCheckRequired.required}")
    private Boolean manualCheckRequired;

    private String correctAnswer;

    @Valid
    private List<OptionDto> options = new ArrayList<>();
}

