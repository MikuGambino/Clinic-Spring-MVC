package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.dto.forms.SignUpForm;
import com.mikegambino.clinic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name = "success", required = false) boolean success) {
        if (success) {
            model.addAttribute("message", "Вы успешно зарегистрировались!");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("user", new SignUpForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") SignUpForm signUpForm,
                               BindingResult bindingResult) {
        if (userService.existsByEmail(signUpForm.getEmail())) {
            Logger.getAnonymousLogger().info("1");
            bindingResult.rejectValue("email", "error.user", "Аккаунт с данной почтой уже зарегистрирован");
        }
        if (userService.existsByUsername(signUpForm.getUsername())) {
            Logger.getAnonymousLogger().info("2");
            bindingResult.rejectValue("username", "error.user", "Аккаунт с данным логином уже существует");
        }

        if (bindingResult.hasErrors()) {
            Logger.getAnonymousLogger().info(bindingResult.getAllErrors().toString());
            return "signup";
        }

        userService.addUser(signUpForm);

        return "redirect:/login?success=true";
    }
}
