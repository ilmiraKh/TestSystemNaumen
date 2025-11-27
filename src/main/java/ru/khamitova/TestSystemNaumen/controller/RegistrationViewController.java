package ru.khamitova.TestSystemNaumen.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.khamitova.TestSystemNaumen.dto.UserRegistrationDto;
import ru.khamitova.TestSystemNaumen.exception.EntityAlreadyExistsException;
import ru.khamitova.TestSystemNaumen.exception.InvalidRoleException;
import ru.khamitova.TestSystemNaumen.service.UserService;

@Controller
@RequestMapping("/registration")
public class RegistrationViewController {
    private final UserService userService;

    @Autowired
    public RegistrationViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String registrationPage(Model model){
        model.addAttribute("user", new UserRegistrationDto());
        return "registration";
    }

    @PostMapping
    public String registrationPost(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                                   BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try{
            userService.register(dto);

        } catch (EntityAlreadyExistsException ex) {
            bindingResult.rejectValue("email", ex.getMessage());
            return "registration";
        }  catch (InvalidRoleException ex) {
            bindingResult.rejectValue("role", ex.getMessage());
            return "registration";
        }

        return "redirect:/login";
    }
}
