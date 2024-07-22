package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.User;
import com.mikegambino.clinic.security.CurrentUser;
import com.mikegambino.clinic.security.UserPrincipal;
import com.mikegambino.clinic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@PreAuthorize("hasAuthority('ADMIN')")
public class AccountController {
    private final UserService userService;

    @GetMapping
    public String account(Model model, @RequestParam(name = "role", required = false) String role,
                           @CurrentUser UserPrincipal currentUser) {

        User user = userService.getByUsername(currentUser.getUsername());
        String currentRole = userService.checkRole(currentUser, role);

        model.addAttribute("user", user);
        model.addAttribute("activePage", "main");
        model.addAttribute("role", currentRole);

        return "account/main";
    }

    @GetMapping("/update")
    public String updateAccountView(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("user", userService.getById(currentUser.getId()));

        return "account/update";
    }

    @PostMapping("/update")
    public String updateAccount(@ModelAttribute("user") User user, @CurrentUser UserPrincipal currentUser) {
        userService.updateUser(user, currentUser);

        return "redirect:/account";
    }
}
