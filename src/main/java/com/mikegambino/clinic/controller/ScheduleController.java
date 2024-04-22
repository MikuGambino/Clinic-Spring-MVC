package com.mikegambino.clinic.controller;

import com.mikegambino.clinic.model.enums.AppointmentStatus;
import com.mikegambino.clinic.model.dto.ScheduleDTO;
import com.mikegambino.clinic.security.CurrentUser;
import com.mikegambino.clinic.security.UserPrincipal;
import com.mikegambino.clinic.service.AppointmentService;
import com.mikegambino.clinic.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;

    public ScheduleController(ScheduleService scheduleService, AppointmentService appointmentService) {
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/{date}/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedules(@PathVariable LocalDate date,
                                                              @PathVariable int doctorId) {
        List<ScheduleDTO> scheduleDTOList = scheduleService.getDoctorScheduleDTOByDate(date, doctorId);
        return new ResponseEntity<>(scheduleDTOList, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ScheduleDTO> addSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO response = scheduleService.addSchedule(scheduleDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        scheduleService.deleteSchedule(id, currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{doctorId}/dates")
    public ResponseEntity<List<LocalDate>> getUsedDates(@PathVariable int doctorId) {
        return ResponseEntity.ok(scheduleService.getScheduleDatesByDoctorAndTime(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/active")
    public ResponseEntity<Set<String>> getActiveAppointments(@PathVariable int doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentDatesByDoctorIdAndStatus(doctorId, AppointmentStatus.ACTIVE));
    }

    @GetMapping("/doctor/{doctorId}/non-active")
    public ResponseEntity<Set<String>> getNonActiveAppointments(@PathVariable int doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentDatesByDoctorIdAndExceptStatus(doctorId, AppointmentStatus.ACTIVE));
    }
}
