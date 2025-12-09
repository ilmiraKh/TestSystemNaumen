package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.service.TeacherResultService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher/results")
public class TeacherResultViewController {
    private final TeacherResultService resultService;
    private final TestService testService;
    private final UserService userService;

    @Autowired
    public TeacherResultViewController(TeacherResultService resultService, TestService testService, UserService userService) {
        this.resultService = resultService;
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping("/check/{id}")
    public String getCheckResultPage(@PathVariable Long id, Principal principal, Model model) {
        User teacher = userService.findByEmail(principal.getName());
        Result result = resultService.getResultForTeacher(id, teacher);

        List<Answer> toCheck = result.getAnswers().stream()
                .filter(a -> a.getQuestion().getManualCheckRequired())
                .toList();

        model.addAttribute("result", result);
        model.addAttribute("answersToCheck", toCheck);

        return "teacher_check_result";
    }

    @PostMapping("/check/{id}")
    public String submitManualCheck(
            @PathVariable Long id,
            @RequestParam Map<String, String> pointsRaw) {
        Map<Long, Double> points = pointsRaw.entrySet().stream()
                .filter(e -> e.getKey().startsWith("answer_"))
                .collect(Collectors.toMap(
                        e -> Long.valueOf(e.getKey().replace("answer_", "")),
                        e -> Double.valueOf(e.getValue())
                ));

        resultService.manualCheck(id, points);
        return "redirect:/teacher/results/" + id;
    }

    @GetMapping("/test/{testId}")
    public String listTestResults(@PathVariable Long testId,
                                  @RequestParam(value = "status", required = false) String status,
                                  Model model,
                                  Principal principal) {

        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        List<Result> results;

        if (status == null || status.isBlank()) {
            results = resultService.findAllByTestAndUser(test, teacher);
            status = "";
        } else {
            try {
                ResultStatus resultStatus = ResultStatus.valueOf(status.toUpperCase());
                results = resultService.findAllByTestAndUserAndStatus(test, teacher, resultStatus);
            } catch (IllegalArgumentException e) {
                results = resultService.findAllByTestAndUser(test, teacher);
                status = "";
            }
        }

        model.addAttribute("test", test);
        model.addAttribute("results", results);
        model.addAttribute("statusFilter", status);

        return "teacher_test_results";
    }

    @GetMapping("/{id}")
    public String getResultPage(@PathVariable Long id, Principal principal, Model model) {
        User teacher = userService.findByEmail(principal.getName());
        Result result = resultService.getResultForTeacher(id, teacher);
        ResultUtil.selectOptions(result);

        model.addAttribute("result", result);
        return "result";
    }
}
