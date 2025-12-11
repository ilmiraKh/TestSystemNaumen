package ru.khamitova.TestSystemNaumen.mapper;

import org.springframework.stereotype.Component;
import ru.khamitova.TestSystemNaumen.dto.OptionDto;
import ru.khamitova.TestSystemNaumen.dto.QuestionDto;
import ru.khamitova.TestSystemNaumen.entity.Option;
import ru.khamitova.TestSystemNaumen.entity.Question;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public QuestionDto toDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setType(question.getType());
        dto.setPoints(question.getPoints());
        dto.setManualCheckRequired(question.getManualCheckRequired());
        dto.setCorrectAnswer(question.getCorrectAnswer());

        if (question.getOptions() != null) {
            dto.setOptions(question.getOptions().stream()
                    .map(this::toOptionDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private OptionDto toOptionDto(Option option) {
        OptionDto dto = new OptionDto();
        dto.setId(option.getId());
        dto.setText(option.getText());
        dto.setIsCorrect(option.getIsCorrect());
        return dto;
    }

    public Question fromDto(QuestionDto dto) {
        Question question = new Question();
        question.setId(dto.getId());
        question.setText(dto.getText());
        question.setType(dto.getType());
        question.setPoints(dto.getPoints());
        question.setManualCheckRequired(dto.getManualCheckRequired());
        question.setCorrectAnswer(dto.getCorrectAnswer());

        if (dto.getOptions() != null) {
            List<Option> options = dto.getOptions().stream()
                    .map(this::fromOptionDto)
                    .collect(Collectors.toList());
            question.setOptions(options);
            options.forEach(o -> o.setQuestion(question));
        }

        return question;
    }

    private Option fromOptionDto(OptionDto dto) {
        Option option = new Option();
        option.setId(dto.getId());
        option.setText(dto.getText());
        option.setIsCorrect(dto.getIsCorrect());
        return option;
    }
}
