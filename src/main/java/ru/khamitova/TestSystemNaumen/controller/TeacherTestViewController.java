package ru.khamitova.TestSystemNaumen.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.TopicService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/teacher/tests")
public class TeacherTestViewController {
    private final TestService testService;
    private final TopicService topicService;
    private final UserService userService;

    @Autowired
    public TeacherTestViewController(TestService testService,
                              TopicService topicService,
                              UserService userService) {
        this.testService = testService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping
    public String listCreatedTests(Model model, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        model.addAttribute("tests", testService.findAllByUser(teacher));
        return "teacher_tests";
    }

    @GetMapping("/create/{topicId}")
    public String createTestPage(@PathVariable Long topicId,
                                 Model model,
                                 Principal principal) {

        User teacher = userService.findByEmail(principal.getName());
        Topic topic = topicService.findById(topicId);

        Test test = new Test();
        test.setTopic(topic);
        test.setUser(teacher);

        model.addAttribute("test", test);
        return "test_form";
    }

    @PostMapping("/create/{topicId}")
    public String createTestPost(@Valid @ModelAttribute("test") Test test,
                           @PathVariable Long topicId,
                           BindingResult bindingResult,
                           Principal principal) {
        if (bindingResult.hasErrors()) {
            return "test_form";
        }

        User teacher = userService.findByEmail(principal.getName());
        Topic topic = topicService.findById(topicId);

        test.setTopic(topic);
        test.setUser(teacher);

        testService.create(test);

        return "redirect:/teacher/tests";
    }

    @GetMapping("/edit/{id}")
    public String editTestPage(@PathVariable Long id, Model model, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());

        Test test = testService.findByIdAndUser(id, teacher);
        model.addAttribute("test", test);
        return "test_form";
    }

    @PostMapping("/edit/{id}")
    public String editTestPost(@PathVariable Long id,
                             @Valid @ModelAttribute("test") Test test,
                             BindingResult bindingResult,
                             Principal principal) {
        if (bindingResult.hasErrors()) {
            return "test_form";
        }

        User teacher = userService.findByEmail(principal.getName());
        test.setId(id);

        testService.update(test, teacher);

        return "redirect:/teacher/tests";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         Principal principal) {
        User teacher = userService.findByEmail(principal.getName());

        testService.deleteByIdAndUser(id, teacher);

        return "redirect:/teacher/tests";
    }
}


