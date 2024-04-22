package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.Treatment;
import com.mikegambino.clinic.model.dto.TreatmentRequest;
import com.mikegambino.clinic.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@RequestMapping("/specializations/{specId}/treatments")
@PreAuthorize("hasAuthority('ADMIN')")
public class TreatmentController {
    private final TreatmentService treatmentService;
    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping("/add")
    public String addTreatmentView(@PathVariable int specId, Model model) {
        Treatment treatment = new Treatment();
        model.addAttribute("treatmentRequest", treatment);
        model.addAttribute("specId", specId);
        return "treatment/add-treatment";
    }

    @PostMapping
    public String addTreatment(@PathVariable("specId") int specId,
                               @ModelAttribute("treatmentRequest") @Valid TreatmentRequest treatmentRequest,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specId", specId);
            return "treatment/add-treatment";
        }

        treatmentService.addTreatment(treatmentRequest, specId);
        return "redirect:/specializations/{specId}/update";
    }

    @PostMapping("{id}/delete")
    public String deleteTreatment(@PathVariable("id") int id,
                                  @PathVariable("specId") int specId) {
        treatmentService.deleteTreatment(id);
        return "redirect:/specializations/{specId}/update";
    }

    @GetMapping("{id}/update")
    public String updateTreatmentView(@PathVariable int id, @PathVariable("specId") int specId,  Model model) {
        model.addAttribute("treatmentRequest", treatmentService.getTreatmentRequest(id));
        model.addAttribute("treatmentId", id);
        model.addAttribute("specId", specId);
        Logger.getAnonymousLogger().info(treatmentService.getTreatmentRequest(id).toString());
        return "treatment/update-treatment";
    }

    @PostMapping("/{id}")
    public String updateTreatment(@PathVariable int id,
                                 @PathVariable int specId,
                                 @ModelAttribute("treatmentRequest") @Valid TreatmentRequest treatmentRequest,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("treatmentId", id);
            model.addAttribute("specId", specId);
            return "treatment/update-treatment";
        }

        treatmentService.updateTreatment(id, treatmentRequest);
        return "redirect:/specializations/{specId}/update";
    }
}
