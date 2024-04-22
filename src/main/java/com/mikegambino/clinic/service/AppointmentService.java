package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.AccessDeniedException;
import com.mikegambino.clinic.exception.BadRequestException;
import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.*;
import com.mikegambino.clinic.model.CancelledAppointment;
import com.mikegambino.clinic.model.enums.AppointmentStatus;
import com.mikegambino.clinic.model.roles.RoleName;
import com.mikegambino.clinic.model.dto.AppointmentRequest;
import com.mikegambino.clinic.repository.*;
import com.mikegambino.clinic.security.UserPrincipal;
import static com.mikegambino.clinic.util.AppConstants.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TreatmentRepository treatmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final CancelledAppointmentRepository cancelledAppointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository,
                              TreatmentRepository treatmentRepository, ScheduleRepository scheduleRepository, PatientRepository patientRepository, CancelledAppointmentRepository cancelledAppointmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.treatmentRepository = treatmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.patientRepository = patientRepository;
        this.cancelledAppointmentRepository = cancelledAppointmentRepository;
    }

    @Transactional
    public int addAppointment(int doctorId, AppointmentRequest appointmentRequest, UserPrincipal currentUser) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(DOCTOR, ID, doctorId));
        Schedule schedule = scheduleRepository.findById(appointmentRequest.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(SCHEDULE, ID, appointmentRequest.getScheduleId()));
        Patient patient = patientRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT, ID, currentUser.getId()));

        if (!doctor.getId().equals(schedule.getDoctor().getId())) {
            throw new BadRequestException("The schedule does not belong to this doctor");
        }
        if (!doctor.isEnabled()) {
            throw new BadRequestException("Doctor is not enabled");
        }
        
        List<Treatment> treatments = appointmentRequest.getTreatments().stream()
                .filter(Objects::nonNull)
                .map(treatmentContainer -> treatmentRepository.findById(treatmentContainer.getTreatmentId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        BigDecimal price = treatments.stream()
                .map(treatment -> treatment.getPriceWithMaxDiscount(patient.isBeneficiary()))
                .reduce(BigDecimal::add).
                orElse(BigDecimal.ZERO);

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .schedule(schedule)
                .startDateTime(schedule.getStartWork())
                .endDateTime(schedule.getEndWork())
                .price(price)
                .status(AppointmentStatus.ACTIVE)
                .treatments(treatments).build();

        if (appointmentRequest.getComment() != null && !appointmentRequest.getComment().isBlank()) {
            appointment.setComment(appointmentRequest.getComment());
        }

        appointmentRepository.save(appointment);

        schedule.setEnabled(false);
        scheduleRepository.save(schedule);

        return appointment.getId();
    }

    public Appointment getAppointment(int id, UserPrincipal currentUser) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(APPOINTMENT, ID, id));

        if (currentUser.getId().equals(appointment.getDoctor().getId()) ||
            currentUser.getId().equals(appointment.getPatient().getId()) ||
            currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            return appointment;
        }

        throw new AccessDeniedException();
    }

    public CancelledAppointment getCancelledAppointment(int id, UserPrincipal currentUser) {
        CancelledAppointment cancelledAppointment = cancelledAppointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CANCELLED_APPOINTMENT, ID, id));

        Appointment appointment = getAppointment(id, currentUser);

        return cancelledAppointment;
    }

    @Transactional
    public void cancelAppointment(int appointmentId, UserPrincipal currentUser, String reason) {
        Appointment appointment = getAppointment(appointmentId, currentUser);
        AppointmentStatus status;

        if (appointment.getDoctor().getId().equals(currentUser.getId())) {
            status = AppointmentStatus.REJECTED_DOCTOR;
        } else {
            status = AppointmentStatus.REJECTED_PATIENT;
        }

        appointment.setStatus(status);

        CancelledAppointment cancelledAppointment = CancelledAppointment.builder()
                .id(appointment.getId())
                .reason(reason)
                .rejectedBy(status.getStatus())
                .rejectTime(LocalDateTime.now())
                .build();

        cancelledAppointmentRepository.save(cancelledAppointment);
        appointmentRepository.save(appointment);
    }

    public void appointmentIsActive(Appointment appointment) {
        if (!appointment.getStatus().equals(AppointmentStatus.ACTIVE)) {
            throw new BadRequestException("Appointment is not active");
        }
    }

    @Transactional
    public void transferAppointment(int id, int appointmentTimeId, String reason, UserPrincipal currentUser) {
        Appointment appointment = getAppointment(id, currentUser);
        Schedule newSchedule = scheduleRepository.findById(appointmentTimeId)
                .orElseThrow(() -> new ResourceNotFoundException(SCHEDULE, ID, appointmentTimeId));

        appointmentIsActive(appointment);

        if (!newSchedule.isEnabled()) {
            throw new BadRequestException("Schedule is not enabled");
        }

        Schedule oldSchedule = appointment.getSchedule();
        oldSchedule.setEnabled(true);
        scheduleRepository.save(oldSchedule);

        newSchedule.setEnabled(false);

        appointment.setStartDateTime(newSchedule.getStartWork());
        appointment.setEndDateTime(newSchedule.getEndWork());
        appointment.setSchedule(newSchedule);
        if (reason != null && !reason.isBlank()) {
            String comment = appointment.getComment();
            if (comment == null) comment = "";
            comment = "Запись перенесена. Причина: " + reason + ". " + comment;
            appointment.setComment(comment);
        }

        appointmentRepository.save(appointment);
    }
    
    public void acceptAppointment(int appointmentId, String diagnosis, UserPrincipal currentUser) {
        Appointment appointment = getAppointment(appointmentId, currentUser);

        appointmentIsActive(appointment);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDiagnosis(diagnosis);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getByDoctorIdAndStatusAndDate(int id, AppointmentStatus status, LocalDate dateTime) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(id))
                .filter(a -> a.getStartDateTime().toLocalDate().equals(dateTime))
                .filter(a -> a.getStatus().equals(status))
                .toList();
    }

    public List<Appointment> getByDoctorIdAndStatus(int id, AppointmentStatus status) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(id))
                .filter(a -> a.getStatus().equals(status))
                .toList();
    }

    public List<Appointment> getByDoctorIdAndExceptStatus(int id, AppointmentStatus status) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(id))
                .filter(a -> !a.getStatus().equals(status))
                .toList();
    }

    public List<Appointment> getByDoctorIdAndDateAndExceptStatus(int id, LocalDate date, AppointmentStatus status) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(id))
                .filter(a -> a.getStartDateTime().toLocalDate().equals(date))
                .filter(a -> !a.getStatus().equals(status))
                .toList();
    }

    public Set<String> getAppointmentDatesByDoctorIdAndStatus(int doctorId, AppointmentStatus appointmentStatus) {
        return appointmentsToDateSet(getByDoctorIdAndStatus(doctorId, appointmentStatus));
    }

    public Set<String> getAppointmentDatesByDoctorIdAndExceptStatus(int doctorId, AppointmentStatus appointmentStatus) {
        return appointmentsToDateSet(getByDoctorIdAndExceptStatus(doctorId, appointmentStatus));
    }

    private Set<String> appointmentsToDateSet(List<Appointment> appointments) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return appointments.stream()
                .map(Appointment::getStartDateTime)
                .map(dateFormat::format)
                .collect(Collectors.toSet());
    }

    public List<Appointment> getActiveAppointmentsByPatientId(int patientId) {
        return appointmentRepository.findByPatientAndStatus(patientId, AppointmentStatus.ACTIVE);
    }

    public List<Appointment> getHistoryAppointmentsByPatientId(int patientId) {
        return appointmentRepository.findByPatientAndExceptStatus(patientId, AppointmentStatus.ACTIVE);
    }
}
