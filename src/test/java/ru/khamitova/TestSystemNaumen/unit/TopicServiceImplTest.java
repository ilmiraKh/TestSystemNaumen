package ru.khamitova.TestSystemNaumen.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.khamitova.TestSystemNaumen.entity.Topic;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.repository.TopicRepository;
import ru.khamitova.TestSystemNaumen.service.impl.TopicServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {
    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicServiceImpl topicService;

    @Test
    void createSuccess() {
        Topic topic = new Topic();
        topic.setName("Java");

        when(topicRepository.existsByName("Java")).thenReturn(false);
        when(topicRepository.save(topic)).thenReturn(topic);

        Topic result = topicService.create(topic);

        assertEquals("Java", result.getName());
        verify(topicRepository).save(topic);
    }

    @Test
    void createException() {
        Topic topic = new Topic();
        topic.setName("Java");

        when(topicRepository.existsByName("Java")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> topicService.create(topic));
    }

    @Test
    void deleteByIdSuccess() {
        Topic topic = new Topic();
        topic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));

        topicService.deleteById(1L);

        verify(topicRepository).delete(topic);
    }

    @Test
    void deleteByIdException() {
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> topicService.deleteById(1L));
    }
}
