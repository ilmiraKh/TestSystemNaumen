package ru.khamitova.TestSystemNaumen.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.service.TopicService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public List<Topic> listTopics(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        Page<Topic> topicsPage = topicService.getPageByName(search,page, pageSize);

        return topicsPage.stream()
                .collect(Collectors.toList());
    }
}
