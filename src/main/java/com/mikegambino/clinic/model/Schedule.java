package com.mikegambino.clinic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "doctor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;
    @Column(name = "start_work")
    private LocalDateTime startWork;
    @Column(name = "end_work")
    private LocalDateTime endWork;
    @Column(name = "is_enabled")
    private boolean isEnabled;
}
