package com.mikegambino.clinic.model.enums;

import lombok.Getter;

@Getter
public enum AppointmentStatus {
    REJECTED_PATIENT("Приём отменен пациентом"),
    REJECTED_DOCTOR("Приём отменен врачом"),
    ACTIVE("Запись к врачу активна"),
    COMPLETED("Приём успешно завершён");
    private final String status;

    AppointmentStatus(String status) {
        this.status = status;
    }
}
