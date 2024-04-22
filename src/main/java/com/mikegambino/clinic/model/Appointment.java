package com.mikegambino.clinic.model;

import com.mikegambino.clinic.model.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    @Column(name = "timestamp_start")
    private LocalDateTime startDateTime;
    @Column(name = "timestamp_end")
    private LocalDateTime endDateTime;
    private BigDecimal price;
    private AppointmentStatus status;
    private String diagnosis;
    private String comment;
    @ManyToMany
    @JoinTable(
            name = "appointment_treatment",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "treatment_id")
    )
    private List<Treatment> treatments;

    public String getFormattedDate(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTimeFormatter.format(localDateTime);
    }

    public String getFormattedStartDateTime() {
        return getFormattedDate(startDateTime);
    }
    public String getFormattedEndDateTime() {
        return getFormattedDate(endDateTime);
    }

    public String getFormattedTreatments() {
        return treatments.stream().map(Treatment::getTitle)
                .collect(Collectors.joining(", "));
    }
}
