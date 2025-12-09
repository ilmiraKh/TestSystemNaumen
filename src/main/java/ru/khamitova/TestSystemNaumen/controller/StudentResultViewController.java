package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.service.ResultService;
import ru.khamitova.TestSystemNaumen.service.UserService;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student/results")
public class StudentResultViewController {
    private final ResultService resultService;
    private final UserService userService;

    @Autowired
    public StudentResultViewController(ResultService resultService, UserService userService) {
        this.resultService = resultService;
        this.userService = userService;
    }

    @GetMapping
    public String getResults(@RequestParam(value = "status", required = false) String status,
                             Principal principal,
                             Model model){
        User student = userService.findByEmail(principal.getName());

        List<Result> results;

        if (status == null || status.isBlank()) {
            results = resultService.findAllByUser(student);
            status = "";
        } else {
            try {
                ResultStatus resultStatus = ResultStatus.valueOf(status.toUpperCase());
                results = resultService.findAllByUserAndStatus(student, resultStatus);
            } catch (IllegalArgumentException e) {
                results = resultService.findAllByUser(student);
                status = "";
            }
        }

        model.addAttribute("results", results);
        model.addAttribute("statusFilter", status);
        return "student_results";
    }

    @GetMapping("/{resultId}")
    public String viewResult(@PathVariable Long resultId, Principal principal, Model model) {
        User student = userService.findByEmail(principal.getName());
        Result result = resultService.findByIdAndUser(resultId, student);

        ResultUtil.selectOptions(result);

        model.addAttribute("result", result);
        return "result";
    }
}
