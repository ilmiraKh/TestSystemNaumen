package ru.khamitova.TestSystemNaumen.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.repository.TestRepository;
import ru.khamitova.TestSystemNaumen.service.impl.TestServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {
    @Mock
    private TestRepository testRepository;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void findAllByUserSuccess() {
        User teacher = new User();
        List<ru.khamitova.TestSystemNaumen.entity.Test> list = List.of(
                new ru.khamitova.TestSystemNaumen.entity.Test(),
                new ru.khamitova.TestSystemNaumen.entity.Test()
        );

        when(testRepository.findAllByUser(teacher)).thenReturn(list);

        List<ru.khamitova.TestSystemNaumen.entity.Test> result = testService.findAllByUser(teacher);

        assertEquals(2, result.size());
    }

    @Test
    void findByIdAndUserSuccess() {
        User teacher = new User();
        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        when(testRepository.findByIdAndUser(1L, teacher)).thenReturn(Optional.of(test));

        ru.khamitova.TestSystemNaumen.entity.Test result = testService.findByIdAndUser(1L, teacher);

        assertNotNull(result);
    }


    @Test
    void findByIdAndUserException() {
        when(testRepository.findByIdAndUser(anyLong(), any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,() -> testService.findByIdAndUser(1L, new User()));
    }

    @Test
    void findByIdSuccess() {
        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        test.setPublished(true);

        when(testRepository.findById(1L)).thenReturn(Optional.of(test));

        ru.khamitova.TestSystemNaumen.entity.Test result = testService.findById(1L);

        assertTrue(result.getPublished());
    }

    @Test
    void findByIdNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,() -> testService.findById(1L));
    }

    @Test
    void findByIdNotPublishedException() {
        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        test.setPublished(false);

        when(testRepository.findById(1L)).thenReturn(Optional.of(test));

        assertThrows(EntityNotFoundException.class,() -> testService.findById(1L));
    }

    @Test
    void createSuccess() {
        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        when(testRepository.save(test)).thenReturn(test);

        ru.khamitova.TestSystemNaumen.entity.Test result = testService.create(test);

        assertNotNull(result);
        verify(testRepository).save(test);
    }

    @Test
    void updateSuccess() {
        User teacher = new User();
        teacher.setId(1L);

        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        test.setId(1L);
        test.setTitle("New");
        test.setDescription("desc");
        test.setPublished(true);

        ru.khamitova.TestSystemNaumen.entity.Test existing = new ru.khamitova.TestSystemNaumen.entity.Test();
        existing.setId(1L);
        existing.setUser(teacher);

        when(testRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(testRepository.save(existing)).thenReturn(existing);

        ru.khamitova.TestSystemNaumen.entity.Test result = testService.update(test, teacher);

        assertEquals("New", result.getTitle());
        assertEquals("desc", result.getDescription());
        verify(testRepository).save(existing);
    }

    @Test
    void deleteByIdAndUserSuccess() {
        User teacher = new User();
        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();

        when(testRepository.findByIdAndUser(1L, teacher)).thenReturn(Optional.of(test));

        testService.deleteByIdAndUser(1L, teacher);

        verify(testRepository).delete(test);
    }

    @Test
    void deleteByIdAndUserException() {
        when(testRepository.findByIdAndUser(anyLong(), any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> testService.deleteByIdAndUser(1L, new User()));
    }

    @Test
    void getPageByFiltersSuccess() {
        Page<ru.khamitova.TestSystemNaumen.entity.Test> page = new PageImpl<>(List.of(new ru.khamitova.TestSystemNaumen.entity.Test()));

        when(testRepository.findByFilters(any(), any(), any(), any()))
                .thenReturn(page);

        Page<ru.khamitova.TestSystemNaumen.entity.Test> result = testService.getPageByFilters(null, null, null, 1, 10);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void publishSuccess() {
        User teacher = new User();
        teacher.setId(1L);

        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        test.setId(1L);
        test.setUser(teacher);
        test.setPublished(false);

        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(testRepository.save(test)).thenReturn(test);

        testService.publish(test, teacher);

        assertTrue(test.getPublished());
    }

    @Test
    void publishException() {
        ru.khamitova.TestSystemNaumen.entity.Test test = new ru.khamitova.TestSystemNaumen.entity.Test();
        test.setPublished(true);

        assertThrows(IllegalStateException.class, () -> testService.publish(test, new User()));
    }
    
}
