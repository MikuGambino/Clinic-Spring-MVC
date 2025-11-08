package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.User;
import com.mikegambino.clinic.model.dto.DoctorRequest;
import com.mikegambino.clinic.model.dto.DoctorResponse;
import com.mikegambino.clinic.model.roles.Role;
import com.mikegambino.clinic.model.roles.RoleName;
import com.mikegambino.clinic.service.DoctorService;
import com.mikegambino.clinic.service.PatientService;
import com.mikegambino.clinic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    private final UserService userService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @GetMapping
    public String users(@RequestParam(value = "role", required = false) String role, Model model){
        if (role != null && !role.equals("all")) {
            model.addAttribute("users", userService.getUsersByRole(role));
        } else {
            model.addAttribute("users", userService.getAllUsers());
        }

        model.addAttribute("activePage", "users");
        return "admin/users";
    }

    @GetMapping("/{id}")
    public String manageUser(@PathVariable int id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        if (user.getRoles().contains(new Role(RoleName.ROLE_DOCTOR))) {
            model.addAttribute("doctor", doctorService.getDoctorOrNull(id));
        }
        if (user.getRoles().contains(new Role(RoleName.ROLE_PATIENT))) {
            model.addAttribute("patient", patientService.getPatientOrNull(id));
        }
        return "admin/manage-user";
    }

    @PostMapping("/{id}/addPatientRole")
    public String givePatientRole(@PathVariable int id) {
        userService.addPatientRole(id);
        return "redirect:/users/{id}";
    }

    @PostMapping("/{id}/removePatientRole")
    public String removePatientRole(@PathVariable int id) {
        userService.removePatientRole(id);
        return "redirect:/users/{id}";
    }

    @GetMapping("/{id}/addDoctorRole")
    public String addDoctorRoleView(@PathVariable int id, Model model) {
        if (doctorService.doctorExist(id)) {
            DoctorResponse doctor = doctorService.getDoctorResponse(id);
            doctor.setEndWorkDate(null);
            model.addAttribute("doctor", doctor);
        } else {
            model.addAttribute("doctor", new DoctorRequest());
        }

        model.addAttribute("doctorId", id);

        return "admin/give-doctor-role";
    }

    @PostMapping("/{id}/addDoctorRole")
    public String addDoctorRole(@ModelAttribute("doctor") @Valid DoctorRequest doctorRequest,
                                BindingResult bindingResult,
                                @PathVariable int id,
                                @RequestParam(value = "image", required = false) MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "admin/give-doctor-role";
        }

        userService.addDoctorRole(id, doctorRequest, file);
        return "redirect:/users/{id}";
    }

    @PostMapping("/{id}/removeDoctorRole")
    public String removeDoctorRole(@PathVariable int id, Model model) {
        userService.removeDoctorRole(id);
        return "redirect:/users/{id}";
    }
}
