package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.service.ResultService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;
import java.util.*;

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
            return "redirect:/student/results/" + existingResult.get().getId();
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

            return "redirect:/student/results/" + result.getId();
        } catch (EntityAlreadyExistsException ex){
            return "redirect:/student/results/" + ex.getExistingId();
        }
    }

}
