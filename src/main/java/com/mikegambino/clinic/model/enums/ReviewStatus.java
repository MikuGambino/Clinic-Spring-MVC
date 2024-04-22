package com.mikegambino.clinic.model.enums;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    MODERATED("В модерации"),
    ACCEPTED("Подтвержденный");

    private final String status;

    ReviewStatus(String status) {
        this.status = status;
    }

}
