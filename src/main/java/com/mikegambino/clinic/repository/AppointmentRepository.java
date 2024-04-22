package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Appointment;
import com.mikegambino.clinic.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends ListCrudRepository<Appointment, Integer> {

    @Query("""
            select a
            from Appointment a
            where a.doctor.id = :doctorId
            and a.patient.id = :patientId
            order by a.startDateTime""")
    List<Appointment> findByDoctorAndPatient(@Param("doctorId") int doctorId, @Param("patientId") int patientId);

    @Query("""
            select a
            from Appointment a
            where a.patient.id = :patientId
            and a.status = :status
            order by a.startDateTime
            """)
    List<Appointment> findByPatientAndStatus(int patientId, AppointmentStatus status);

    @Query("""
            select a
            from Appointment a
            where a.patient.id = :patientId
            and a.status <> :status
            order by a.startDateTime
            """)
    List<Appointment> findByPatientAndExceptStatus(int patientId, AppointmentStatus status);
}
