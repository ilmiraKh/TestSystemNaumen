package ru.khamitova.TestSystemNaumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.TopicService;


@Controller
@RequestMapping("/tests")
public class TestViewController {
    private final static int PAGE_SIZE = 20;
    private final TestService testService;
    private final TopicService topicService;

    @Autowired
    public TestViewController(TestService testService, TopicService topicService) {
        this.testService = testService;
        this.topicService = topicService;
    }

    @GetMapping
    public String listTests(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "topicId", required = false) Long topicId,
                            @RequestParam(value = "topicName", required = false) String topicName,
                            Model model) {

        if (topicId != null) {
            Topic topic = topicService.findById(topicId);
            if (topic != null) {
                topicName = topic.getName();
            }
            model.addAttribute("topicId", topicId);
        }

        if (page < 1) {
            return "redirect:/tests?page=1"
                    + (search != null ? "&search=" + search : "")
                    + (topicId != null ? "&topicId=" + topicId : "")
                    + (topicName != null ? "&topicName=" + topicName : "");
        }

        Page<Test> testsPage = testService.getPageByFilters(search, topicId, topicName, page, PAGE_SIZE);

        int totalPages = testsPage.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
            page = 1;
        }

        if (page > totalPages) {
            return "redirect:/tests?page=" + totalPages
                    + (search != null ? "&search=" + search : "")
                    + (topicId != null ? "&topicId=" + topicId : "")
                    + (topicName != null ? "&topicName=" + topicName : "");
        }

        model.addAttribute("tests", testsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        model.addAttribute("topicName", topicName);

        return "tests";
    }

}
