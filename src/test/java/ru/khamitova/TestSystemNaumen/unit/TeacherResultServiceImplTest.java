package ru.khamitova.TestSystemNaumen.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.khamitova.TestSystemNaumen.entity.*;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;
import ru.khamitova.TestSystemNaumen.repository.ResultRepository;
import ru.khamitova.TestSystemNaumen.service.impl.TeacherResultServiceImpl;
import ru.khamitova.TestSystemNaumen.util.ResultUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherResultServiceImplTest {
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private ResultUtil resultUtil;
    @InjectMocks
    private TeacherResultServiceImpl teacherResultService;
    private User teacher;
    private Test test;

    @BeforeEach
    void setup() {
        teacher = new User();
        teacher.setId(10L);

        test = new Test();
        test.setId(1L);
        test.setUser(teacher);
    }

    @org.junit.jupiter.api.Test
    void getResultForTeacherSuccess() {
        Result result = new Result();
        result.setId(1L);
        result.setTest(test);

        when(resultRepository.findByIdAndTest_User(1L, teacher)).thenReturn(Optional.of(result));

        Result fetched = teacherResultService.getResultForTeacher(1L, teacher);

        assertEquals(result, fetched);
        verify(resultUtil).selectOptions(result);
    }

    @org.junit.jupiter.api.Test
    void getResultForTeacherException() {
        when(resultRepository.findByIdAndTest_User(99L, teacher)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> teacherResultService.getResultForTeacher(99L, teacher));
    }

    @org.junit.jupiter.api.Test
    void manualCheckSuccess() {
        Question question1 = new Question();
        question1.setId(1L);
        question1.setManualCheckRequired(true);

        Question question2 = new Question();
        question2.setId(2L);
        question2.setManualCheckRequired(false);

        Answer answer1 = new Answer();
        answer1.setId(101L);
        answer1.setQuestion(question1);

        Answer answer2 = new Answer();
        answer2.setId(102L);
        answer2.setQuestion(question2);
        answer2.setPointsAwarded(3.0);

        Result result = new Result();
        result.setId(500L);
        result.setScore(5.0);
        result.setAnswers(List.of(answer1, answer2));

        when(resultRepository.findById(500L)).thenReturn(Optional.of(result));

        Map<Long, Double> points = Map.of(101L, 4.0);

        teacherResultService.manualCheck(500L, points);

        assertEquals(5.0 + 4.0, result.getScore());
        assertEquals(4.0, answer1.getPointsAwarded());
        assertEquals(ResultStatus.CHECKED, result.getStatus());

        verify(resultRepository).save(result);
    }

    @org.junit.jupiter.api.Test
    void manualCheckException() {
        when(resultRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> teacherResultService.manualCheck(123L, Map.of()));
    }

    @org.junit.jupiter.api.Test
    void findAllByTestAndUserSuccess() {
        List<Result> list = List.of(new Result(), new Result());

        when(resultRepository.findAllByTestAndTest_User(test, teacher))
                .thenReturn(list);

        List<Result> result = teacherResultService.findAllByTestAndUser(test, teacher);

        assertEquals(list, result);
    }

    @org.junit.jupiter.api.Test
    void findAllByTestAndUserAndStatusSuccess() {
        List<Result> list = List.of(new Result());

        when(resultRepository.findAllByTestAndTest_UserAndStatus(test, teacher, ResultStatus.CHECKED))
                .thenReturn(list);

        List<Result> result = teacherResultService.findAllByTestAndUserAndStatus(test, teacher, ResultStatus.CHECKED);

        assertEquals(list, result);
    }

}
