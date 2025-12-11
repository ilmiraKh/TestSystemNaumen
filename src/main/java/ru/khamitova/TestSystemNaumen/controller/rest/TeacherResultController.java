package ru.khamitova.TestSystemNaumen.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.dto.AnswerDto;
import ru.khamitova.TestSystemNaumen.dto.ResultDto;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.mapper.ResultMapper;
import ru.khamitova.TestSystemNaumen.service.TeacherResultService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher/results")
public class TeacherResultController {
    private final TeacherResultService resultService;
    private final TestService testService;
    private final UserService userService;
    private final ResultMapper resultMapper;

    @Autowired
    public TeacherResultController(TeacherResultService resultService,
                                   TestService testService,
                                   UserService userService,
                                   ResultMapper resultMapper) {
        this.resultService = resultService;
        this.testService = testService;
        this.userService = userService;
        this.resultMapper = resultMapper;
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Map<String, Object>> getCheckResult(@PathVariable Long id, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Result result = resultService.getResultForTeacher(id, teacher);

        List<AnswerDto> toCheck = result.getAnswers().stream()
                .filter(a -> Boolean.TRUE.equals(a.getQuestion().getManualCheckRequired()))
                .map(resultMapper::toAnswerDto)
                .toList();

        Map<String, Object> response = Map.of(
                "result", resultMapper.toTeacherCheckDto(result),
                "answersToCheck", toCheck
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/check/{id}")
    public ResponseEntity<Void> submitManualCheck(
            @PathVariable Long id,
            @RequestBody Map<Long, Double> points) {
        resultService.manualCheck(id, points);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<ResultDto>> listTestResults(
            @PathVariable Long testId,
            @RequestParam(value = "status", required = false) String status,
            Principal principal) {

        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        List<Result> results;

        if (status == null || status.isBlank()) {
            results = resultService.findAllByTestAndUser(test, teacher);
        } else {
            try {
                ResultStatus resultStatus = ResultStatus.valueOf(status.toUpperCase());
                results = resultService.findAllByTestAndUserAndStatus(test, teacher, resultStatus);
            } catch (IllegalArgumentException e) {
                results = resultService.findAllByTestAndUser(test, teacher);
            }
        }

        List<ResultDto> dtos = results.stream()
                .map(resultMapper::toTeacherCheckDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto> getResult(@PathVariable Long id, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Result result = resultService.getResultForTeacher(id, teacher);
        return ResponseEntity.ok(resultMapper.toDto(result));
    }
}

