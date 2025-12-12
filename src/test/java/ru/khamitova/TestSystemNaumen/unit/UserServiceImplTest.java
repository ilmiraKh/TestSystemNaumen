package ru.khamitova.TestSystemNaumen.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.khamitova.TestSystemNaumen.dto.UserRegistrationDto;
import ru.khamitova.TestSystemNaumen.entity.User;
import ru.khamitova.TestSystemNaumen.entity.enums.Role;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.exception.InvalidRoleException;
import ru.khamitova.TestSystemNaumen.repository.UserRepository;
import ru.khamitova.TestSystemNaumen.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserRegistrationDto dto;
    private static final String EMAIL = "test@gmail.com";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail(EMAIL);

        dto = new UserRegistrationDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail(EMAIL);
        dto.setPassword("12345");
        dto.setRole(Role.STUDENT);
    }

    @Test
    public void findByEmailSuccess(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = userService.findByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void findByEmailException(){
        String badEmail = "notfound@gmail.com";
        when(userRepository.findByEmail(badEmail))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findByEmail(badEmail));
        verify(userRepository, times(1)).findByEmail(badEmail);
    }

    @Test
    public void registerSuccess(){
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("123");

        userService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();

        assertEquals(dto.getFirstName(), saved.getFirstName());
        assertEquals(dto.getLastName(), saved.getLastName());
        assertEquals(dto.getEmail(), saved.getEmail());
        assertEquals("123", saved.getPassword());
        assertEquals(dto.getRole(), saved.getRole());
    }

    @Test
    void registerAlreadyExistsException() {
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }


    @Test
    public void registerInvalidRoleException(){
        dto.setRole(Role.ADMIN);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        assertThrows(InvalidRoleException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }
}
