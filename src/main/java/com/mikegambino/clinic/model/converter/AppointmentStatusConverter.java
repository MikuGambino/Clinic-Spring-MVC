package com.mikegambino.clinic.model.converter;

import com.mikegambino.clinic.model.enums.AppointmentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@Converter(autoApply = true)
public class AppointmentStatusConverter implements AttributeConverter<AppointmentStatus, String> {
    @Override
    public String convertToDatabaseColumn(AppointmentStatus appointmentStatus) {
        if (appointmentStatus == null) return null;
        return appointmentStatus.getStatus();
    }

    @Override
    public AppointmentStatus convertToEntityAttribute(String status) {
        if (status == null) return null;
        return Stream.of(AppointmentStatus.values())
                .filter(s -> s.getStatus().equals(status))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
