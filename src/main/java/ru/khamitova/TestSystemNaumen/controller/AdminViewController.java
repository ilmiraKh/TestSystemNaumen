package ru.khamitova.TestSystemNaumen.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.service.TopicService;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    private final TopicService topicService;
    @Autowired
    public AdminViewController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/topics/create")
    public String createTopicForm(Model model) {
        model.addAttribute("topic", new Topic());
        return "topic_form";
    }

    @PostMapping("/topics/create")
    public String saveTopic(@Valid @ModelAttribute("topic") Topic topic,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "topic_form";
        }

        try {
            topicService.create(topic);
        } catch (EntityAlreadyExistsException ex) {
            bindingResult.rejectValue("name", ex.getMessage());
            return "topic_form";
        }

        return "redirect:/topics";
    }

    @GetMapping("/topics/edit/{id}")
    public String editTopic(@PathVariable Long id, Model model) {
        Topic topic = topicService.findById(id);
        model.addAttribute("topic", topic);
        return "topic_form";
    }

    @PostMapping("/topics/edit/{id}")
    public String updateTopic(
            @PathVariable Long id,
            @Valid @ModelAttribute("topic") Topic topic,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "topic_form";
        }

        topic.setId(id);
        try {
            topicService.update(topic);
        } catch (EntityAlreadyExistsException ex) {
            result.rejectValue("name", ex.getMessage());
            return "topic_form";
        }

        return "redirect:/topics";
    }

    @PostMapping("/topics/delete/{id}")
    public String deleteTopic(@PathVariable Long id) {
        topicService.deleteById(id);
        return "redirect:/topics";
    }
}
