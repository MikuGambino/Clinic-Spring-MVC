package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Appointment;
import com.mikegambino.clinic.model.CancelledAppointment;
import org.springframework.data.repository.ListCrudRepository;

public interface CancelledAppointmentRepository extends ListCrudRepository<CancelledAppointment, Integer> {
}
