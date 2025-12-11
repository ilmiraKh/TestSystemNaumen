package ru.khamitova.TestSystemNaumen.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.dto.ResultDto;
import ru.khamitova.TestSystemNaumen.dto.StudentTestDto;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.mapper.ResultMapper;
import ru.khamitova.TestSystemNaumen.mapper.TestMapper;
import ru.khamitova.TestSystemNaumen.service.ResultService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/student/tests")
public class StudentTestController {
    private final TestService testService;
    private final ResultService resultService;
    private final UserService userService;
    private final TestMapper testMapper;
    private final ResultMapper resultMapper;

    @Autowired
    public StudentTestController(TestService testService, ResultService resultService, UserService userService, TestMapper testMapper, ResultMapper resultMapper) {
        this.testService = testService;
        this.resultService = resultService;
        this.userService = userService;
        this.testMapper = testMapper;
        this.resultMapper = resultMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTest(@PathVariable Long id, Principal principal) {
        Test test = testService.findById(id);
        User student = userService.findByEmail(principal.getName());

        Optional<Result> existingResult = resultService.checkExistingResult(test, student);
        if (existingResult.isPresent()) {
            return ResponseEntity.status(409).body(Map.of(
                    "error", "test.alreadyPassed",
                    "resultId", existingResult.get().getId()
            ));
        }

        StudentTestDto testDto = testMapper.toStudentDto(test);
        return ResponseEntity.ok(testDto);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ResultDto> submitTest(@PathVariable Long id,
                                                Principal principal,
                                                @RequestBody Map<Long, List<String>> answersMap) {
        Test test = testService.findById(id);
        User student = userService.findByEmail(principal.getName());

        Map<String, String[]> answers = new HashMap<>();
        answersMap.forEach((questionId, values) ->
                answers.put(questionId.toString(), values.toArray(new String[0]))
        );

        Result result = resultService.create(student, test, answers);
        ResultDto dto = resultMapper.toDto(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

    }
}
