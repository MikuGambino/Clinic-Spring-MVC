package com.mikegambino.clinic.model.dto.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewForm {
    @Range(min = 0, message = "Оценка не должна быть пустой!")
    private int score;
    private String description;
}
