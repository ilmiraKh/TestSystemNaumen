package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.service.ResultService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student/tests")
public class StudentTestViewController {
    private final TestService testService;
    private final ResultService resultService;
    private final UserService userService;

    @Autowired
    public StudentTestViewController(TestService testService, ResultService resultService, UserService userService) {
        this.testService = testService;
        this.resultService = resultService;
        this.userService = userService;
    }

    @GetMapping("/take/{testId}")
    public String takeTest(@PathVariable Long testId,
                           Principal principal,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Test test = testService.findById(testId);
        User student = userService.findByEmail(principal.getName());

        Optional<Result> existingResult = resultService.checkExistingResult(test, student);
        if (existingResult.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "test.alreadyPassed");
            return "redirect:/student/tests/result/" + existingResult.get().getId();
        }

        model.addAttribute("test", test);
        return "student_test";
    }

    @PostMapping("/submit/{id}")
    public String submitTest(@PathVariable Long id, Principal principal, @RequestParam MultiValueMap<String, String> params) {
        Test test = testService.findById(id);
        User student = userService.findByEmail(principal.getName());

        try {
            Map<String, String[]> answers = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                List<String> values = entry.getValue();
                answers.put(entry.getKey(), values.toArray(new String[0]));
            }

            Result result = resultService.create(student, test, answers);

            return "redirect:/student/tests/result/" + result.getId();
        } catch (EntityAlreadyExistsException ex){
            return "redirect:/student/tests/result/" + ex.getExistingId();
        }
    }

    @GetMapping("/result/{resultId}")
    public String viewResult(@PathVariable Long resultId, Principal principal, Model model) {
        User student = userService.findByEmail(principal.getName());
        Result result = resultService.findByIdAndUser(resultId, student);

        ResultUtil.selectOptions(result);

        model.addAttribute("result", result);
        return "result";
    }

}
