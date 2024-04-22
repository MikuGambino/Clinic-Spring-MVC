package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.dto.forms.ReviewForm;
import com.mikegambino.clinic.security.CurrentUser;
import com.mikegambino.clinic.security.UserPrincipal;
import com.mikegambino.clinic.service.PatientService;
import com.mikegambino.clinic.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final PatientService patientService;
    public ReviewController(ReviewService reviewService, PatientService patientService) {
        this.reviewService = reviewService;
        this.patientService = patientService;
    }

    @PostMapping("/doctor/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public String addReview(@PathVariable("id") int doctorId,
                            @CurrentUser UserPrincipal currentUser,
                            @ModelAttribute("reviewForm") @Valid ReviewForm reviewForm,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "doctor/doctor";
        }
        reviewService.addReview(reviewForm, doctorId, currentUser);
        return "redirect:/doctors/{id}";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminReviewsView(Model model) {
        model.addAttribute("reviews", reviewService.getModeratedReviews());
        model.addAttribute("activePage", "reviews");
        return "admin/admin-reviews";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteReview(@PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        reviewService.deleteReview(id, currentUser);
        return "redirect:/reviews/admin";
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public String acceptReview(@PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        reviewService.acceptReview(id, currentUser);
        return "redirect:/reviews/admin";
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public String userReviewsView(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("patient", patientService.getPatient(currentUser));
        model.addAttribute("activePage", "reviews");
        return "patient/patient-reviews";
    }
}