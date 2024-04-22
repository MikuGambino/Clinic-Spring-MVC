package com.mikegambino.clinic.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorRequest {
    @NotNull(message = "Имя не может быть пустым")
    @Size(min = 3, message = "Минимальная длина имени - 3 символа")
    private String name;
    @NotNull(message = "Фамилия не может быть пустым")
    @Size(min = 3, message = "Минимальная длина фамилии - 3 символа")
    private String surname;
    private String patronymic;
    @NotNull(message = "Описание не может быть пустым")
    @Size(min = 3, message = "Минимальная длина описания - 3 символа")
    private String description;
    @NotNull(message = "Выберите статус врача")
    private boolean isEnabled;

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
