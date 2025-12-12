package ru.khamitova.TestSystemNaumen.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.khamitova.TestSystemNaumen.entity.Option;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;
import ru.khamitova.TestSystemNaumen.repository.QuestionRepository;
import ru.khamitova.TestSystemNaumen.service.impl.QuestionServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {
    @Mock
    private QuestionRepository questionRepository;
    @InjectMocks
    private QuestionServiceImpl questionService;
    private User teacher;
    private ru.khamitova.TestSystemNaumen.entity.Test test;

    @BeforeEach
    void setup() {
        teacher = new User();
        teacher.setId(1L);

        test = new ru.khamitova.TestSystemNaumen.entity.Test();
        test.setId(100L);
        test.setUser(teacher);
        test.setPublished(false);
    }

    @Test
    void findAllByTestSuccess() {
        Question question1 = new Question();
        question1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Question question2 = new Question();
        question2.setCreatedAt(LocalDateTime.now());

        when(questionRepository.findAllByTest(test)).thenReturn(List.of(question2, question1));

        List<Question> result = questionService.findAllByTest(test);

        assertEquals(question1, result.get(0));
        assertEquals(question2, result.get(1));
    }

    @Test
    void findByIdAndUserSuccess() {
        Question question = new Question();
        question.setTest(test);

        when(questionRepository.findById(10L)).thenReturn(Optional.of(question));

        Question result = questionService.findByIdAndUser(10L, teacher);

        assertEquals(question, result);
    }

    @Test
    void findByIdAndUserException() {
        User anotherTeacher = new User();
        anotherTeacher.setId(99L);

        Question question = new Question();
        question.setTest(test);

        when(questionRepository.findById(10L)).thenReturn(Optional.of(question));

        assertThrows(EntityNotFoundException.class, () -> questionService.findByIdAndUser(10L, anotherTeacher));
    }

    @Test
    void createSuccess() {
        Question question = new Question();
        question.setTest(test);
        question.setType(QuestionType.CHOICE);

        Option option = new Option();
        option.setText("option");
        option.setIsCorrect(true);

        question.setOptions(List.of(option));

        when(questionRepository.save(any())).thenReturn(question);

        Question result = questionService.create(question, teacher);

        assertFalse(result.getManualCheckRequired());
        assertEquals(1, result.getOptions().size());
        assertEquals(question, result);
    }

    @Test
    void createException() {
        User another = new User();
        another.setId(999L);

        Question question = new Question();
        question.setTest(test);
        question.setType(QuestionType.OPEN);

        assertThrows(EntityNotFoundException.class, () -> questionService.create(question, another));
    }

    @Test
    void createTestPublishedException() {
        test.setPublished(true);

        Question question = new Question();
        question.setTest(test);
        question.setType(QuestionType.OPEN);

        assertThrows(IllegalStateException.class, () -> questionService.create(question, teacher));
    }

    @Test
    void createOpenQuestionRequireManualCheck() {
        Question question = new Question();
        question.setTest(test);
        question.setType(QuestionType.OPEN);
        question.setCorrectAnswer(""); 

        when(questionRepository.save(any())).thenReturn(question);

        Question result = questionService.create(question, teacher);

        assertTrue(result.getManualCheckRequired());
    }

    @Test
    void createChoiceQuestionException() {
        Question question = new Question();
        question.setTest(test);
        question.setType(QuestionType.CHOICE);

        Option o = new Option();
        o.setText("A");
        o.setIsCorrect(false);

        question.setOptions(List.of(o));

        assertThrows(IllegalArgumentException.class, () -> questionService.create(question, teacher));
    }

    @Test
    void updateSuccess() {
        Question question = new Question();
        question.setId(1L);
        question.setTest(test);
        question.setType(QuestionType.CHOICE);
        question.setOptions(new ArrayList<>());

        Question update = new Question();
        update.setId(1L);
        update.setTest(test);
        update.setType(QuestionType.OPEN);
        update.setCorrectAnswer("text");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any())).thenReturn(question);

        Question result = questionService.update(update, teacher);

        assertEquals(QuestionType.OPEN, result.getType());
        assertEquals("text", result.getCorrectAnswer());
        assertTrue(result.getOptions().isEmpty());
    }

    @Test
    void updateChoiceSuccess() {
        Question existing = new Question();
        existing.setId(1L);
        existing.setTest(test);
        existing.setType(QuestionType.OPEN);

        Question question = new Question();
        question.setId(1L);
        question.setTest(test);
        question.setType(QuestionType.CHOICE);

        Option o = new Option();
        o.setText("A");
        o.setIsCorrect(true);
        question.setOptions(List.of(o));

        when(questionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(questionRepository.save(any())).thenReturn(existing);

        Question result = questionService.update(question, teacher);

        assertEquals(QuestionType.CHOICE, result.getType());
        assertEquals(1, result.getOptions().size());
    }

    @Test
    void updateException() {
        User another = new User();
        another.setId(999L);

        Question question = new Question();
        question.setId(1L);
        question.setTest(test);

        Question existing = new Question();
        existing.setId(1L);
        existing.setTest(test);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(EntityNotFoundException.class, () -> questionService.update(question, another));
    }

    @Test
    void deleteByIdAndUserSuccess() {
        Question question = new Question();
        question.setId(1L);
        question.setTest(test);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        questionService.deleteByIdAndUser(1L, teacher);

        verify(questionRepository).delete(question);
    }

    @Test
    void deleteByIdAndUserException() {
        User another = new User();
        another.setId(999L);

        Question question = new Question();
        question.setId(1L);
        question.setTest(test);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        assertThrows(EntityNotFoundException.class, () -> questionService.deleteByIdAndUser(1L, another));
    }

    @Test
    void deleteByIdAndUserTestPublishedException() {
        test.setPublished(true);

        Question question = new Question();
        question.setId(1L);
        question.setTest(test);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        assertThrows(IllegalStateException.class, () ->
                questionService.deleteByIdAndUser(1L, teacher)
        );
    }
}
