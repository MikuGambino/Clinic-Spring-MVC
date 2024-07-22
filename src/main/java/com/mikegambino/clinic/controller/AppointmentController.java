package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.Appointment;
import com.mikegambino.clinic.model.dto.AppointmentRequest;
import com.mikegambino.clinic.model.enums.AppointmentStatus;
import com.mikegambino.clinic.security.CurrentUser;
import com.mikegambino.clinic.security.UserPrincipal;
import com.mikegambino.clinic.service.AppointmentService;
import com.mikegambino.clinic.service.DoctorService;
import com.mikegambino.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @GetMapping("/doctor/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public String addAppointmentView(@PathVariable int id, Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("hasBeneficiary", patientService.getPatient(currentUser).isBeneficiary());
        model.addAttribute("doctor", doctorService.getDoctorResponse(id));
        model.addAttribute("appointment", new AppointmentRequest());
        return "appointment/appointment-add";
    }

    @PostMapping("/doctor/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public String addAppointment(@PathVariable(value = "id") int doctorId,
                                 @ModelAttribute("appointment") @Valid AppointmentRequest appointmentRequest,
                                 BindingResult bindingResult,
                                 @CurrentUser UserPrincipal currentUser) {
        if (bindingResult.hasErrors()) {
            return "appointment/appointment-add";
        }

        int appointmentId = appointmentService.addAppointment(doctorId, appointmentRequest, currentUser);
        return "redirect:/appointments/" + appointmentId + "?status=success";
    }

    @GetMapping("/{id}")
    public String appointmentView(@PathVariable("id") int id,
                                  @RequestParam(value = "status", required = false) String status,
                                  @CurrentUser UserPrincipal currentUser,
                                  Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }

        Appointment appointment = appointmentService.getAppointment(id, currentUser);
        model.addAttribute("appointment", appointment);

        AppointmentStatus appointmentStatus = appointment.getStatus();
        if (appointmentStatus.equals(AppointmentStatus.REJECTED_DOCTOR)
            || appointmentStatus.equals(AppointmentStatus.REJECTED_PATIENT)) {
            model.addAttribute("cancelledAppointment", appointmentService.getCancelledAppointment(id, currentUser));
        }
        model.addAttribute("isActive", appointment.getStatus().equals(AppointmentStatus.ACTIVE));

        return "appointment/appointment-info";
    }

    @GetMapping("/doctor/active")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorActiveAppointments(@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                         @CurrentUser UserPrincipal currentUser, Model model) {
        if (date == null) {
            model.addAttribute("appointments",
                    appointmentService.getByDoctorIdAndStatus(currentUser.getId(), AppointmentStatus.ACTIVE));
        } else {
            model.addAttribute("date", " | " + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(date));
            model.addAttribute("appointments",
                    appointmentService.getByDoctorIdAndStatusAndDate(currentUser.getId(), AppointmentStatus.ACTIVE, date));
        }

        model.addAttribute("doctor", doctorService.getDoctorResponse(currentUser.getId()));
        model.addAttribute("activePage", "activeApp");
        return "doctor/active-appointments";
    }

    @GetMapping("/doctor/history")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorHistoryAppointments(@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                         @CurrentUser UserPrincipal currentUser, Model model) {
        if (date == null) {
            model.addAttribute("appointments",
                    appointmentService.getByDoctorIdAndExceptStatus(currentUser.getId(), AppointmentStatus.ACTIVE));
        } else {
            model.addAttribute("date", " | " + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(date));
            model.addAttribute("appointments",
                    appointmentService.getByDoctorIdAndDateAndExceptStatus(currentUser.getId(), date, AppointmentStatus.ACTIVE));
        }

        model.addAttribute("doctor", doctorService.getDoctorResponse(currentUser.getId()));
        model.addAttribute("activePage", "history");
        return "doctor/history-appointments";
    }

    @GetMapping("/patient/active")
    @PreAuthorize("hasRole('PATIENT')")
    public String patientActiveAppointments(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("activePage", "activeApp");
        model.addAttribute("appointments", appointmentService.getActiveAppointmentsByPatientId(currentUser.getId()));
        return "patient/active-appointments";
    }

    @GetMapping("/patient/history")
    @PreAuthorize("hasRole('PATIENT')")
    public String patientHistoryAppointments(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("activePage", "history");
        model.addAttribute("appointments", appointmentService.getHistoryAppointmentsByPatientId(currentUser.getId()));
        return "patient/history-appointments";
    }

    @GetMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') || hasRole('DOCTOR')")
    public String cancelAppointmentView(@PathVariable int id, Model model,
                                        @CurrentUser UserPrincipal currentUser) {

        Appointment appointment = appointmentService.getAppointment(id, currentUser);
        appointmentService.appointmentIsActive(appointment);

        model.addAttribute("appointment", appointment);

        return "appointment/appointment-reject";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") int id,
                                    @RequestParam("reason") String reason,
                                    @CurrentUser UserPrincipal currentUser) {

        appointmentService.cancelAppointment(id, currentUser, reason);
        return "redirect:/appointments/{id}?status=cancelled";
    }

    @GetMapping("/{id}/transfer")
    @PreAuthorize("hasRole('PATIENT') || hasRole('DOCTOR')")
    public String transferAppointmentView(@PathVariable int id, Model model,
                                          @CurrentUser UserPrincipal currentUser) {
        Appointment appointment = appointmentService.getAppointment(id, currentUser);

        appointmentService.appointmentIsActive(appointment);

        model.addAttribute("appointment", appointment);
        model.addAttribute("doctor", appointment.getDoctor());

        return "appointment/appointment-transfer";
    }

    @PostMapping("/{id}/transfer")
    public String transferAppointment(@PathVariable("id") int id,
                                      @RequestParam("reason") String reason,
                                      @RequestParam("scheduleId") int scheduleId,
                                      @CurrentUser UserPrincipal currentUser) {
        appointmentService.transferAppointment(id, scheduleId, reason, currentUser);
        return "redirect:/appointments/{id}?status=transferred";
    }

    @GetMapping("/{id}/diagnosis")
    @PreAuthorize("hasRole('DOCTOR')")
    public String setDiagnosisView(@PathVariable int id, @CurrentUser UserPrincipal currentUser, Model model) {
        Appointment appointment = appointmentService.getAppointment(id, currentUser);

        appointmentService.appointmentIsActive(appointment);

        model.addAttribute("appointment", appointment);

        return "appointment/appointment-diagnosis";
    }

    @PostMapping("/{id}/diagnosis")
    @PreAuthorize("hasRole('DOCTOR')")
    public String setDiagnosis(@PathVariable int id, @RequestParam("diagnosis") String diagnosis,
                               @CurrentUser UserPrincipal currentUser) {
        appointmentService.acceptAppointment(id, diagnosis, currentUser);

        return "redirect:/appointments/doctor/active";
    }
}
