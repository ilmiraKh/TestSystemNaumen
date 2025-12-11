package ru.khamitova.TestSystemNaumen.mapper;

import org.springframework.stereotype.Component;
import ru.khamitova.TestSystemNaumen.dto.StudentQuestionDto;
import ru.khamitova.TestSystemNaumen.dto.StudentTestDto;
import ru.khamitova.TestSystemNaumen.dto.TeacherTestDto;
import ru.khamitova.TestSystemNaumen.dto.TestDto;
import ru.khamitova.TestSystemNaumen.entity.Option;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TestMapper {

    public StudentTestDto toStudentDto(Test test) {
        if (test == null) return null;

        StudentTestDto dto = new StudentTestDto();
        dto.setId(test.getId());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setTopicId(test.getTopic().getId());
        dto.setTopicName(test.getTopic().getName());

        List<StudentQuestionDto> questions = test.getQuestions().stream()
                .map(this::toQuestionDto)
                .collect(Collectors.toList());

        dto.setQuestions(questions);
        return dto;
    }

    private StudentQuestionDto toQuestionDto(Question question) {
        StudentQuestionDto dto = new StudentQuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());

        Map<Long, String> optionsMap = question.getOptions().stream()
                .collect(Collectors.toMap(Option::getId, Option::getText));

        dto.setOptions(optionsMap);
        return dto;
    }

    public TeacherTestDto toTeacherDto(Test test) {
        if (test == null) return null;

        TeacherTestDto dto = new TeacherTestDto();
        dto.setId(test.getId());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setTopicId(test.getTopic().getId());
        dto.setPublished(test.getPublished());

        return dto;
    }

    public Test fromTeacherDto(TeacherTestDto dto) {
        Test test = new Test();
        test.setId(dto.getId());
        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setPublished(dto.getPublished());

        return test;
    }
}

