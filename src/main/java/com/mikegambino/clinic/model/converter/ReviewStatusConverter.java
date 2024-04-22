package com.mikegambino.clinic.model.converter;

import com.mikegambino.clinic.model.enums.ReviewStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class ReviewStatusConverter implements AttributeConverter<ReviewStatus, String> {
    @Override
    public String convertToDatabaseColumn(ReviewStatus reviewStatus) {
        if (reviewStatus == null) {
            return null;
        }
        return reviewStatus.getStatus();
    }

    @Override
    public ReviewStatus convertToEntityAttribute(String status) {
        if (status == null) {
            return null;
        }

        return Stream.of(ReviewStatus.values())
                .filter(s -> s.getStatus().equals(status))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
