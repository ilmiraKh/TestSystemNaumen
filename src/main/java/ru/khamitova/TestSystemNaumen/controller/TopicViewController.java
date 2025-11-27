package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.khamitova.TestSystemNaumen.service.TopicService;

@Controller
@RequestMapping("/topics")
public class TopicViewController {
    private final TopicService topicService;
    @Autowired
    public TopicViewController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public String listTopics(Model model) {
        model.addAttribute("topics", topicService.getAll());
        return "topics";
    }
}
