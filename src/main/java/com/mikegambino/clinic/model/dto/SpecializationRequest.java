package com.mikegambino.clinic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class SpecializationRequest {
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 3, message = "Название не может быть меньше 3 символов")
    private String title;
    private String description;
}

