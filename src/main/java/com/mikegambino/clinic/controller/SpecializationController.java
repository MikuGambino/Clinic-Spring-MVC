package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.dto.SpecializationRequest;
import com.mikegambino.clinic.service.SpecializationService;
import com.mikegambino.clinic.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/specializations")
@PreAuthorize("hasAuthority('ADMIN')")
public class SpecializationController {
    private final SpecializationService specializationService;
    private final TreatmentService treatmentService;
    public SpecializationController(SpecializationService specializationService, TreatmentService treatmentService) {
        this.specializationService = specializationService;
        this.treatmentService = treatmentService;
    }

    @GetMapping("/admin")
    public String specializationsAdmin(Model model) {
        model.addAttribute("specializations", specializationService.getAllSpecializations());
        model.addAttribute("activePage", "specializations");
        return "specialization/admin-specializations";
    }

    @GetMapping("/add")
    public String addSpecializationView(Model model) {
        model.addAttribute("specialization", new Specialization());
        return "specialization/add-specialization";
    }

    @PostMapping
    public String addSpecialization(@ModelAttribute("specialization") @Valid SpecializationRequest specializationRequest,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "specialization/add-specialization";
        }

        int id = specializationService.addSpecialization(specializationRequest).getId();
        return "redirect:/specializations/" + id + "/update";
    }

    @GetMapping("/{id}/update")
    public String updateSpecializationView(@PathVariable(value = "id") int id, Model model,
                                           @RequestParam(value = "success", required = false) boolean success) {
        model.addAttribute("specialization", specializationService.getSpecializationRequest(id));
        model.addAttribute("treatments", treatmentService.getTreatmentsBySpecialization(id));
        model.addAttribute("specId", id);
        model.addAttribute("success", success);

        return "specialization/update-specialization";
    }

    @PostMapping("/{id}")
    public String updateSpecialization(@ModelAttribute("specialization") @Valid SpecializationRequest specializationRequest,
                                     @PathVariable int id,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "specialization/update-specialization";
        }
        specializationService.updateSpecialization(id, specializationRequest);

        return "redirect:/specializations/{id}/update?success=true";
    }

    @PostMapping("{id}/delete")
    public String deleteSpecialization(@PathVariable("id") int id) {
        specializationService.deleteSpecialization(id);
        return "redirect:/specializations/admin";
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('DOCTOR') || hasAuthority('PATIENT')")
    @GetMapping
    public String specializations(Model model) {
        model.addAttribute("specializations", specializationService.getUsedSpecializations());
        return "specialization/specializations";
    }
}
