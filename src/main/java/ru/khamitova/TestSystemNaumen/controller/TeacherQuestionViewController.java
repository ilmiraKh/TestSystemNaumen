package ru.khamitova.TestSystemNaumen.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.service.QuestionService;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/teacher/tests/{testId}/questions")
public class TeacherQuestionViewController {
    private final QuestionService questionService;
    private final TestService testService;
    private final UserService userService;

    @Autowired
    public TeacherQuestionViewController(QuestionService questionService, TestService testService, UserService userService) {
        this.questionService = questionService;
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping
    public String listQuestions(@PathVariable Long testId, Model model, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);
        model.addAttribute("test", test);
        model.addAttribute("questions", questionService.findAllByTest(test));
        return "teacher_test_questions";
    }

    @GetMapping("/create")
    public String createQuestionForm(@PathVariable Long testId, Model model, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        Question question = new Question();
        question.setTest(test);
        model.addAttribute("question", question);
        return "question_form";
    }

    @PostMapping("/create")
    public String saveQuestion(@PathVariable Long testId,
                               @Valid @ModelAttribute("question") Question question,
                               BindingResult result,
                               Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(testId, teacher);

        question.setTest(test);

        if (result.hasErrors()) {
            return "question_form";
        }

        try {
            questionService.create(question, teacher);
        } catch (IllegalArgumentException ex) {
            result.rejectValue("options", ex.getMessage());
            return "question_form";
        }

        return "redirect:/teacher/tests/" + testId + "/questions";
    }

    @GetMapping("/edit/{id}")
    public String editQuestionForm(@PathVariable Long testId,
                                   @PathVariable Long id,
                                   Model model,
                                   Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Question question = questionService.findByIdAndUser(id, teacher);
        model.addAttribute("question", question);
        return "question_form";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Long testId,
                                 @PathVariable Long id,
                                 @Valid @ModelAttribute("question") Question question,
                                 BindingResult result,
                                 Principal principal,
                                 Model model) {
        User teacher = userService.findByEmail(principal.getName());
        question.setId(id);

        Test test = testService.findByIdAndUser(testId, teacher);
        question.setTest(test);

        if (result.hasErrors()) {
            return "question_form";
        }

        try {
            questionService.update(question, teacher);
        } catch (IllegalArgumentException ex) {
            result.rejectValue("options", ex.getMessage());
            model.addAttribute("question", question);
            return "question_form";
        }

        return "redirect:/teacher/tests/" + testId + "/questions";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long testId,
                                 @PathVariable Long id,
                                 Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        questionService.deleteByIdAndUser(id, teacher);
        return "redirect:/teacher/tests/" + testId + "/questions";
    }
}
