package com.mikegambino.clinic.model.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Schedule;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ScheduleDTO {
    private int id;
    private int doctorId;
    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private boolean isEnabled;

    public ScheduleDTO(Schedule schedule) {
        this.id = schedule.getId();
        this.doctorId = schedule.getDoctor().getId();
        this.startWork = schedule.getStartWork();
        this.endWork = schedule.getEndWork();
        this.isEnabled = schedule.isEnabled();
    }
}
