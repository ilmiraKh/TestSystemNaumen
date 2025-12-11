package ru.khamitova.TestSystemNaumen.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.dto.ResultDto;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.mapper.ResultMapper;
import ru.khamitova.TestSystemNaumen.service.ResultService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student/results")
public class StudentResultController {
    private final ResultService resultService;
    private final UserService userService;
    private final ResultMapper resultMapper;

    @Autowired
    public StudentResultController(ResultService resultService, UserService userService, ResultMapper resultMapper) {
        this.resultService = resultService;
        this.userService = userService;
        this.resultMapper = resultMapper;
    }

    @GetMapping
    public ResponseEntity<List<ResultDto>> getResults(@RequestParam(value = "status", required = false) String status,
                                                      Principal principal) {

        User student = userService.findByEmail(principal.getName());
        List<Result> results;

        if (status == null || status.isBlank()) {
            results = resultService.findAllByUser(student);
        } else {
            try {
                ResultStatus resultStatus = ResultStatus.valueOf(status.toUpperCase());
                results = resultService.findAllByUserAndStatus(student, resultStatus);
            } catch (IllegalArgumentException e) {
                results = resultService.findAllByUser(student);
            }
        }

        return ResponseEntity.ok(
                results.stream().map(resultMapper::toDto).toList()
        );
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<ResultDto> getResult(@PathVariable Long resultId, Principal principal) {
        User student = userService.findByEmail(principal.getName());
        Result result = resultService.findByIdAndUser(resultId, student);

        ResultDto dto = resultMapper.toDto(result);

        return ResponseEntity.ok(dto);
    }
}
