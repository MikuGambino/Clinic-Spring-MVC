package com.mikegambino.clinic.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AppointmentRequest {
    @NotNull(message = "Выберите услугу")
    private List<TreatmentContainer> treatments;
    @NotNull(message = "Выберите время приёма")
    private Integer scheduleId;
    private String comment;
}
