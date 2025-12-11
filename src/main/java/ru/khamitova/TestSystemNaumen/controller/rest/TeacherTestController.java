package ru.khamitova.TestSystemNaumen.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.dto.TeacherTestDto;
import ru.khamitova.TestSystemNaumen.dto.TestDto;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.mapper.TestMapper;
import ru.khamitova.TestSystemNaumen.service.TestService;
import ru.khamitova.TestSystemNaumen.service.TopicService;
import ru.khamitova.TestSystemNaumen.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher/tests")
public class TeacherTestController {
    private final TestService testService;
    private final TopicService topicService;
    private final UserService userService;
    private final TestMapper testMapper;

    @Autowired
    public TeacherTestController(TestService testService,
                                 TopicService topicService,
                                 UserService userService,
                                 TestMapper testMapper) {
        this.testService = testService;
        this.topicService = topicService;
        this.userService = userService;
        this.testMapper = testMapper;
    }

    @GetMapping
    public ResponseEntity<List<TeacherTestDto>> listCreatedTests(Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        List<Test> tests = testService.findAllByUser(teacher);
        List<TeacherTestDto> dtos = tests.stream()
                .map(testMapper::toTeacherDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create/{topicId}")
    public ResponseEntity<TeacherTestDto> createTest(@PathVariable Long topicId,
                                              @Valid @RequestBody TeacherTestDto testDto,
                                              Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Topic topic = topicService.findById(topicId);

        Test test = testMapper.fromTeacherDto(testDto);
        test.setTopic(topic);
        test.setUser(teacher);

        testService.create(test);

        return ResponseEntity.status(HttpStatus.CREATED).body(testMapper.toTeacherDto(test));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherTestDto> getTest(@PathVariable Long id, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(id, teacher);
        return ResponseEntity.ok(testMapper.toTeacherDto(test));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherTestDto> editTest(@PathVariable Long id,
                                            @Valid @RequestBody TeacherTestDto testDto,
                                            Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test existing = testService.findByIdAndUser(id, teacher);

        Test test = testMapper.fromTeacherDto(testDto);
        test.setId(id);
        test.setUser(teacher);
        test.setTopic(existing.getTopic());

        testService.update(test, teacher);

        return ResponseEntity.ok(testMapper.toTeacherDto(test));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        testService.deleteByIdAndUser(id, teacher);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<Void> publish(@PathVariable Long id, Principal principal) {
        User teacher = userService.findByEmail(principal.getName());
        Test test = testService.findByIdAndUser(id, teacher);
        testService.publish(test, teacher);
        return ResponseEntity.noContent().build();
    }
}

