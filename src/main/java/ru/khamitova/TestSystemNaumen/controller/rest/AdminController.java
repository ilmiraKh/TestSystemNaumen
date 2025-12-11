package ru.khamitova.TestSystemNaumen.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.service.TopicService;


@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final TopicService topicService;

    @Autowired
    public AdminController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/topics")
    public ResponseEntity<?> createTopic(@Valid @RequestBody Topic topic) {
        Topic saved = topicService.create(topic);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable Long id, @Valid @RequestBody Topic topic) {
        topic.setId(id);
        Topic updated = topicService.update(topic);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        topicService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
