package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.AccessDeniedException;
import com.mikegambino.clinic.exception.BadRequestException;
import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Schedule;
import com.mikegambino.clinic.model.dto.ScheduleDTO;
import com.mikegambino.clinic.model.roles.RoleName;
import com.mikegambino.clinic.repository.DoctorRepository;
import com.mikegambino.clinic.repository.ScheduleRepository;
import static com.mikegambino.clinic.util.AppConstants.*;

import com.mikegambino.clinic.security.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<ScheduleDTO> getDoctorScheduleDTOByDate(LocalDate date, int doctorId) {
        List<Schedule> schedules = scheduleRepository.findDoctorScheduleByDate(date, doctorId);

        return scheduleListToDTOs(schedules);
    }

    public ScheduleDTO addSchedule(ScheduleDTO scheduleDTO) {
        Doctor doctor = doctorRepository.findById(scheduleDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(DOCTOR, ID, scheduleDTO.getDoctorId()));

        List<Schedule> schedules = scheduleRepository.findDoctorScheduleByDate(scheduleDTO.getStartWork().toLocalDate(),
                                                                                            scheduleDTO.getDoctorId());
        LocalDateTime startWork = scheduleDTO.getStartWork();
        LocalDateTime endWork = scheduleDTO.getEndWork();

        validateDates(startWork, endWork, schedules);

        Schedule schedule = new Schedule();
        schedule.setEnabled(true);
        schedule.setDoctor(doctor);
        schedule.setStartWork(startWork);
        schedule.setEndWork(endWork);
        scheduleRepository.save(schedule);

        return new ScheduleDTO(schedule);
    }

    private void validateDates(LocalDateTime startWork, LocalDateTime endWork, List<Schedule> schedules) {
        startWork = startWork.plusSeconds(1);
        endWork = endWork.minusSeconds(1);
        for (Schedule schedule : schedules) {
            if (!((startWork.isAfter(schedule.getEndWork()) || endWork.isBefore(schedule.getStartWork()))
                    && startWork.isBefore(endWork))) {
                throw new BadRequestException("Invalid startWorkDate: %s or endWorkDate: %s"
                        .formatted(startWork, endWork));
            }
        }
    }

    public void deleteSchedule(int id, UserPrincipal currentUser) {
        Schedule schedule = getSchedule(id);

        if (schedule.getDoctor().getId().equals(currentUser.getId()) ||
            currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()))) {

            scheduleRepository.delete(schedule);
        }

        throw new AccessDeniedException();
    }

    private List<ScheduleDTO> scheduleListToDTOs(List<Schedule> scheduleList) {
        return scheduleList.stream()
                .map(ScheduleDTO::new)
                .toList();
    }

    private Schedule getSchedule(int id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SCHEDULE, ID, id));

        return schedule;
    }

    public List<LocalDate> getScheduleDatesByDoctorAndTime(int doctorId) {
        List<LocalDate> localDates = scheduleRepository.findScheduleDatesByDoctorAndTime(doctorId, LocalDate.now())
                .stream()
                .map(Date::toLocalDate)
                .toList();
        return localDates;
    }
}
