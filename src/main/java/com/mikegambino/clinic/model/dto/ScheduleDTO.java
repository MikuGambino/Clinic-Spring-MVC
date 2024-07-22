package com.mikegambino.clinic.model.dto;


import com.mikegambino.clinic.model.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

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
