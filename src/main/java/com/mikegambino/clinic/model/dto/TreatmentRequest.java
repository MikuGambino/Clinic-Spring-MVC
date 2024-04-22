package com.mikegambino.clinic.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TreatmentRequest {
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 3, message = "Название не может быть меньше 3 символов")
    private String title;
    private String description;
    @NotNull(message = "Цена не может быть пустой")
    @Min(value = 0, message = "Цена не может быть меньше 0")
    private BigDecimal price;
    @NotNull(message = "Льготная скидка не может быть пустой")
    @Min(value = 0, message = "Льготная скидка не может быть меньше 0%")
    @Max(value = 100, message = "Льготная скидка не может быть больше 100%")
    private Integer beneficiaryDiscount;
    @NotNull(message = "Скидка не может быть пустой")
    @Min(value = 0, message = "Скидка не может быть меньше 0%")
    @Max(value = 100, message = "Скидка не может быть больше 100%")
    private Integer discount;

    public TreatmentRequest(String title, String description, BigDecimal price,
                            Integer beneficiaryDiscount, Integer discount) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.beneficiaryDiscount = beneficiaryDiscount;
        this.discount = discount;
    }
}
