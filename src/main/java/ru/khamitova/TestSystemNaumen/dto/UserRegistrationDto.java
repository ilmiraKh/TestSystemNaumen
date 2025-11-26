package ru.khamitova.TestSystemNaumen.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.khamitova.TestSystemNaumen.entity.enums.Role;
import ru.khamitova.TestSystemNaumen.validation.annotation.PasswordMatch;

@Getter
@Setter
@PasswordMatch
public class UserRegistrationDto {

    @NotBlank(message = "{firstName.notBlank}")
    private String firstName;

    @NotBlank(message = "{lastName.notBlank}")
    private String lastName;

    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.invalid}")
    private String email;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, message = "{password.size}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{password.complexity}"
    )
    private String password;

    @NotBlank(message = "{passwordConfirm.notBlank}")
    private String confirmPassword;

    @NotNull(message = "{role.notNull}")
    private Role role;
}
