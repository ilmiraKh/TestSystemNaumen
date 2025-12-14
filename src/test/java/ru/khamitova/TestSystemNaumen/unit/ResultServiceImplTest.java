package ru.khamitova.TestSystemNaumen.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.khamitova.TestSystemNaumen.entity.Question;
import ru.khamitova.TestSystemNaumen.entity.Result;
import ru.khamitova.TestSystemNaumen.entity.Test;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.repository.ResultRepository;
import ru.khamitova.TestSystemNaumen.service.impl.ResultServiceImpl;
import ru.khamitova.TestSystemNaumen.service.scoring.QuestionScoringStrategy;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultServiceImplTest {
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private ResultUtil resultUtil;
    @Mock
    private QuestionScoringStrategy openStrategy;
    @Mock
    private QuestionScoringStrategy choiceStrategy;
    private ResultServiceImpl resultService;
    private User student;
    private Test test;
    private Question openQuestion;
    private Question choiceQuestion;

    @BeforeEach
    void setup() {
        student = new User();
        student.setId(1L);

        test = new Test();
        test.setId(10L);

        openQuestion = new Question();
        openQuestion.setId(100L);
        openQuestion.setType(QuestionType.OPEN);
        openQuestion.setManualCheckRequired(true);
        openQuestion.setPoints(5);

        choiceQuestion = new Question();
        choiceQuestion.setId(200L);
        choiceQuestion.setType(QuestionType.CHOICE);
        choiceQuestion.setManualCheckRequired(false);
        choiceQuestion.setPoints(3);

        test.setQuestions(List.of(openQuestion, choiceQuestion));

        when(openStrategy.getSupportedType()).thenReturn(QuestionType.OPEN);
        when(choiceStrategy.getSupportedType()).thenReturn(QuestionType.CHOICE);

        resultService = new ResultServiceImpl(
                resultRepository,
                List.of(openStrategy, choiceStrategy),
                resultUtil
        );
    }

    @org.junit.jupiter.api.Test
    void findByIdAndUserSuccess() {
        Result result = new Result();
        result.setId(5L);

        when(resultRepository.findByIdAndUser(5L, student)).thenReturn(Optional.of(result));

        Result r = resultService.findByIdAndUser(5L, student);

        assertEquals(5L, r.getId());
        verify(resultUtil).selectOptions(result);
    }

    @org.junit.jupiter.api.Test
    void findByIdAndUserException() {
        when(resultRepository.findByIdAndUser(5L, student)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> resultService.findByIdAndUser(5L, student));
    }

    @org.junit.jupiter.api.Test
    void createSuccess() {
        Map<String, String[]> answers = new HashMap<>();
        answers.put("question_200", new String[]{"1"});
        answers.put("question_100", new String[]{"my text"});

        when(resultRepository.findByTestAndUser(test, student)).thenReturn(Optional.empty());
        when(choiceStrategy.score(eq(choiceQuestion), any(), any())).thenReturn(3.0);
        when(resultRepository.save(any(Result.class))).thenAnswer(i -> i.getArgument(0));

        Result result = resultService.create(student, test, answers);

        assertEquals(3.0, result.getScore());
        assertEquals(ResultStatus.WAITING, result.getStatus());
        assertEquals(2, result.getAnswers().size());
    }

    @org.junit.jupiter.api.Test
    void createOpenQuestionEmptySuccess() {
        Map<String, String[]> answers = new HashMap<>();
        answers.put("question_100", new String[]{""});
        answers.put("question_200", new String[]{"1"});

        when(resultRepository.findByTestAndUser(test, student)).thenReturn(Optional.empty());
        when(choiceStrategy.score(eq(choiceQuestion), any(), any())).thenReturn(3.0);
        when(resultRepository.save(any(Result.class))).thenAnswer(i -> i.getArgument(0));

        Result result = resultService.create(student, test, answers);

        assertEquals(3.0, result.getScore());
        assertEquals(ResultStatus.WAITING, result.getStatus());
    }

    @org.junit.jupiter.api.Test
    void createException() {
        Result existing = new Result();
        existing.setId(99L);
        when(resultRepository.findByTestAndUser(test, student)).thenReturn(Optional.of(existing));

        Map<String, String[]> answers = new HashMap<>();

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> resultService.create(student, test, answers));

        assertEquals(99L, ex.getExistingId());
    }

    @org.junit.jupiter.api.Test
    void createUnsupportedQuestionTypeException() {
        Question q = new Question();
        q.setId(300L);
        q.setType(null);
        q.setManualCheckRequired(false);
        q.setPoints(1);

        test.setQuestions(List.of(q));

        Map<String, String[]> answers = new HashMap<>();
        answers.put("question_300", new String[]{"x"});

        when(resultRepository.findByTestAndUser(test, student)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> resultService.create(student, test, answers));
    }

    @org.junit.jupiter.api.Test
    void findAllByUserSuccess() {
        Result r = new Result();
        when(resultRepository.findAllByUser(student)).thenReturn(List.of(r));

        List<Result> list = resultService.findAllByUser(student);

        assertEquals(1, list.size());
    }

    @org.junit.jupiter.api.Test
    void findAllByUserAndStatusSuccess() {
        Result r = new Result();
        when(resultRepository.findAllByUserAndStatus(student, ResultStatus.CHECKED))
                .thenReturn(List.of(r));

        List<Result> list = resultService.findAllByUserAndStatus(student, ResultStatus.CHECKED);

        assertEquals(1, list.size());
    }
}
