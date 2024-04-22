package com.mikegambino.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity(name = "cancelled_appointment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelledAppointment {
    @Id
    private Integer id;
    @OneToOne
    @JoinColumn(name = "id")
    private Appointment appointment;
    private String reason;
    @Column(name = "rejected_by")
    private String rejectedBy;
    @Column(name = "reject_time")
    private LocalDateTime rejectTime;

    public String getFormattedRejectTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTimeFormatter.format(this.rejectTime);
    }
}