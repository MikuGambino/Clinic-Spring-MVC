package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.Review;
import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.dto.forms.ReviewForm;
import com.mikegambino.clinic.model.dto.DoctorRequest;
import com.mikegambino.clinic.model.dto.DoctorResponse;
import com.mikegambino.clinic.security.CurrentUser;
import com.mikegambino.clinic.security.UserPrincipal;
import com.mikegambino.clinic.service.DoctorService;
import com.mikegambino.clinic.service.ReviewService;
import com.mikegambino.clinic.service.SpecializationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final SpecializationService specializationService;
    private final ReviewService reviewService;

    public DoctorController(DoctorService doctorService, SpecializationService specializationService,
                            ReviewService reviewService) {
        this.doctorService = doctorService;
        this.specializationService = specializationService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String doctors(@RequestParam(value = "specId", required = false) Integer specId, Model model) {
        if (specId != null) {
            model.addAttribute("specialization", specializationService.getSpecialization(specId));
            model.addAttribute("doctors", doctorService.getDoctorsWithScheduleBySpecialization(specId));
        } else {
            model.addAttribute("doctors", doctorService.getDoctorsWithSchedule());
        }
        return "doctor/doctors";
    }

    @GetMapping("/manage")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorUpdateByDoctorView(@CurrentUser UserPrincipal currentUser, Model model) {
        DoctorResponse doctorResponse = doctorService.getDoctorResponse(currentUser.getId());
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("activePage", "docProfile");

        return "doctor/manage-doctor-by-doctor";
    }

    @GetMapping("/update")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorUpdateByDoctorInfoView(@CurrentUser UserPrincipal currentUser, Model model) {
        DoctorResponse doctorResponse = doctorService.getDoctorResponse(currentUser.getId());
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("activePage", "docProfile");

        return "doctor/update-doctor-by-doctor";
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorUpdateByDoctor(@ModelAttribute("doctor") @Valid DoctorRequest doctorRequest,
                                      BindingResult bindingResult,
                                      @RequestParam(value = "image", required = false) MultipartFile file,
                                      @CurrentUser UserPrincipal currentUser) {
        if (bindingResult.hasErrors()) {
            return "doctor/update-doctor-by-doctor";
        }

        doctorService.updateDoctor(currentUser.getId(), doctorRequest, file, currentUser);
        return "redirect:/doctors/manage";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String doctorsAdminView(@RequestParam(value = "spec", required = false) Integer spec, Model model) {
        if (spec != null && spec != 0) {
            model.addAttribute("doctors", doctorService.getDoctorsBySpecialization(spec));
        } else {
            model.addAttribute("doctors", doctorService.getAllDoctors());
        }

        model.addAttribute("activePage", "doctors");
        model.addAttribute("specializations", specializationService.getAllSpecializations());
        return "admin/admin-doctors";
    }

    @GetMapping("/{id}/manage/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String doctorUpdateByAdminView(@PathVariable int id, Model model) {
        DoctorResponse doctor = doctorService.getDoctorResponse(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("specCount", specializationService.getFreeSpecializationsByDoctor(id).size());
        return "admin/manage-doctor";
    }

    @GetMapping("/{id}/addSpec")
    @PreAuthorize("hasRole('ADMIN')")
    public String addSpecializationView(@PathVariable int id, Model model) {
        List<Specialization> freeSpecs = specializationService.getFreeSpecializationsByDoctor(id);
        if (freeSpecs.isEmpty()) {
            return "redirect:/doctors/{id}/update";
        }
        model.addAttribute("doctorId", id);
        model.addAttribute("specializations", freeSpecs);
        return "doctor/add-specialization";
    }

    @PostMapping("/{id}/addSpec")
    @PreAuthorize("hasRole('ADMIN')")
    public String addSpecialization(@PathVariable int id, @RequestParam("spec") int spec) {
        doctorService.addSpecialization(id, spec);
        return "redirect:/doctors/{id}/manage/admin";
    }

    @PostMapping("/{id}/removeSpec/{specId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeSpecialization(@PathVariable int id, @PathVariable int specId) {
        doctorService.removeSpecialization(id, specId);
        return "redirect:/doctors/{id}/manage/admin";
    }

    @GetMapping("/{id}/update/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String doctorUpdateByAdminInfoView(@PathVariable int id, Model model) {
        DoctorRequest doctorRequest = doctorService.getDoctorRequest(id);
        model.addAttribute("doctor", doctorRequest);
        model.addAttribute("doctorId", id);
        return "admin/update-doctor";
    }

    @PostMapping("/{id}/update/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String doctorUpdateByAdmin(@ModelAttribute("doctor") @Valid DoctorRequest doctorRequest,
                               BindingResult bindingResult,
                               @RequestParam(value = "image", required = false) MultipartFile file,
                               @PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        if (bindingResult.hasErrors()) {
            return "admin/update-doctor";
        }
        doctorService.updateDoctor(id, doctorRequest, file, currentUser);
        return "redirect:/doctors/{id}/manage/admin";
    }

    @GetMapping("/{id}")
    public String doctorView(@PathVariable int id,  Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("doctor", doctorService.getDoctorResponse(id));
        model.addAttribute("reviewForm", new ReviewForm());
        model.addAttribute("reviews", reviewService.getAcceptedReviewsByDoctor(id));
        if (!reviewService.canAddReview(id, currentUser)) {
            Review moderatedReview = reviewService.getModeratedReview(id, currentUser);
            model.addAttribute("patientReview", moderatedReview);
            model.addAttribute("canAddReview", false);
        } else {
            model.addAttribute("canAddReview", true);
        }
        model.addAttribute("canMakeAppointment", doctorService.isDoctorHasSchedule(id));
        return "doctor/doctor";
    }
}
