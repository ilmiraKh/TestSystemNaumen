package ru.khamitova.TestSystemNaumen.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.khamitova.TestSystemNaumen.dto.TestDto;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.mapper.TestMapper;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.TopicService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tests")
public class TestController {
    private final TestService testService;
    private final TopicService topicService;

    @Autowired
    public TestController(TestService testService, TopicService topicService, TestMapper testMapper) {
        this.testService = testService;
        this.topicService = topicService;
    }

    @GetMapping
    public List<TestDto> listTests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "topicName", required = false) String topicName) {

        Page<Test> testsPage = testService.getPageByFilters(search, topicId, topicName, page, pageSize);

        return testsPage.stream()
                .map(test -> new TestDto(
                        test.getId(),
                        test.getTitle(),
                        test.getDescription(),
                        test.getTopic().getId(),
                        test.getTopic().getName()
                ))
                .collect(Collectors.toList());
    }
}
