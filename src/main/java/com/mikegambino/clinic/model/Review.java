package com.mikegambino.clinic.model;

import com.mikegambino.clinic.model.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @Column(name = "call_date")
    private LocalDateTime callDateTime;
    @Column(name = "appointment_date")
    private LocalDate appointmentDate;
    private int score;
    private String description;
    @Column(name = "status")
    private ReviewStatus reviewStatus;

    public String getFormattedCallTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTimeFormatter.format(callDateTime);
    }

    public String getFormattedAppointmentDate() {
        return "Посетил(а): " +
                appointmentDate.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) +
                " " + appointmentDate.getYear() + " г.";
    }
}


