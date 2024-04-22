package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Appointment;
import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends ListCrudRepository<Doctor, Integer> {

    @Query(value = """
            select distinct d.* from doctor d
            inner join schedule sc on d.id = sc.doctor_id
            inner join doctor_specialization ds on ds.doctor_id = d.id
            where ds.specialization_id = :id
            and sc.is_enabled = true
            and sc.start_work > CURRENT_TIMESTAMP""", nativeQuery = true)
    List<Doctor> findDoctorsWithScheduleBySpecialization(@Param("id") int specId);

    @Query(value = """
            select distinct d.* from doctor d
            join schedule sc on d.id = sc.doctor_id
            join doctor_specialization ds on ds.doctor_id = d.id
            and sc.is_enabled = true
            and sc.start_work > CURRENT_TIMESTAMP""", nativeQuery = true)
    List<Doctor> findDoctorsWithSchedule();

    @Query("""
            select case when (count(*) > 0) then true else false end
            from Doctor d
            join Schedule sc on sc.doctor.id = :doctorId
            where sc.startWork > CURRENT_TIMESTAMP and sc.isEnabled = true""")
    boolean isDoctorHasSchedule(int doctorId);
}
