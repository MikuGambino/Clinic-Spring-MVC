package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Schedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends ListCrudRepository<Schedule, Integer> {

    @Query(value = """
            select *
            from schedule s
            where :date = date(s.start_work)
            and s.is_enabled = true and
            s.doctor_id = :doctorId
            order by s.start_work""", nativeQuery = true)
    List<Schedule> findDoctorScheduleByDate(@Param("date") LocalDate date, @Param("doctorId") int doctorId);

    @Query(value = """
            select date(s.start_work)
            from schedule s
            where s.doctor_id = :doctorId
            and date(s.start_work) >= :date
            and s.is_enabled = true
            order by s.start_work""", nativeQuery = true)
    List<Date> findScheduleDatesByDoctorAndTime(int doctorId, LocalDate date);
}
