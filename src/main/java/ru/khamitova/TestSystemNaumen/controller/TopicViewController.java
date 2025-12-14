package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.service.TopicService;

@Controller
@RequestMapping("/topics")
public class TopicViewController {
    private final static int PAGE_SIZE = 20;
    private final TopicService topicService;
    @Autowired
    public TopicViewController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public String listTopics(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(value = "search", required = false) String search,
                             Model model) {
        if (page < 1) {
            return "redirect:/topics?page=1" + (search != null ? "&search=" + search : "");
        }

        Page<Topic> topicsPage = topicService.getPageByName(search, page, PAGE_SIZE);
        int totalPages = topicsPage.getTotalPages();

        if (totalPages == 0) {
            totalPages = 1;
            page = 1;
        }

        if (page > totalPages) {
            return "redirect:/topics?page=" + totalPages + (search != null ? "&search=" + search : "");
        }

        model.addAttribute("topics", topicsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        return "topics";
    }
}
