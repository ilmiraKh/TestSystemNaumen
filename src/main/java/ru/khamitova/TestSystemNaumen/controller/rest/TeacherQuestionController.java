package ru.khamitova.TestSystemNaumen.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.dto.QuestionDto;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.mapper.QuestionMapper;
import ru.khamitova.TestSystemNaumen.service.QuestionService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher/tests/{testId}/questions")
public class TeacherQuestionController {
    private final QuestionService questionService;
    private final TestService testService;
    private final UserService userService;
    private final QuestionMapper questionMapper;

    @Autowired
    public TeacherQuestionController(QuestionService questionService, TestService testService, UserService userService, QuestionMapper questionMapper) {
        this.questionService = questionService;
        this.testService = testService;
        this.userService = userService;
        this.questionMapper = questionMapper;
    }

    @GetMapping
    public ResponseEntity<List<QuestionDto>> listQuestions(@PathVariable Long testId, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        List<Question> questions = questionService.findAllByTest(test);
        List<QuestionDto> dtos = questions.stream()
                .map(questionMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(@PathVariable Long testId,
                                                      @Valid @RequestBody QuestionDto questionDto,
                                                      Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        Question question = questionMapper.fromDto(questionDto);
        question.setTest(test);

        questionService.create(question, teacher);

        QuestionDto createdDto = questionMapper.toDto(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestion(@PathVariable Long testId,
                                                   @PathVariable Long id,
                                                   Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Question question = questionService.findByIdAndUser(id, teacher);
        return ResponseEntity.ok(questionMapper.toDto(question));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long testId,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody QuestionDto questionDto,
                                                      Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        Question question = questionMapper.fromDto(questionDto);
        question.setId(id);
        question.setTest(test);

        questionService.update(question, teacher);
        return ResponseEntity.ok(questionMapper.toDto(question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long testId,
                                               @PathVariable Long id,
                                               Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        questionService.deleteByIdAndUser(id, teacher);
        return ResponseEntity.noContent().build();
    }
}

