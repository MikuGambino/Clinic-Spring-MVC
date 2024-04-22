package com.mikegambino.clinic.model.dto;

import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Specialization;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DoctorResponse {
    private int id;
    private String username;
    private String name;
    private String surname;
    private String patronymic;
    private String description;
    private LocalDate startWorkDate;
    private LocalDate endWorkDate;
    private boolean isEnabled;
    private List<Specialization> specializations;
    private long numberOfReview;
    private double rating;
    private String image;

    public String getFullname() {
        if (patronymic == null) {
            return "%s %s".formatted(surname, name);
        }
        return "%s %s %s".formatted(surname, name, patronymic);
    }

    public String getFormattedSpecializations() {
        String out = specializations.stream()
                .map(Specialization::getTitle)
                .filter(n -> (n != null) && !n.isEmpty())
                .collect(Collectors.joining(", "));
        if (out.isEmpty()) return "Специальностей нет";
        else return out;
    }

    public DoctorResponse(Doctor doctor, IntSummaryStatistics stats) {
        this.id = doctor.getId();
        this.image = doctor.getImage();
        this.username = doctor.getUser().getUsername();
        this.description = doctor.getDescription();
        this.numberOfReview = stats.getCount();
        this.rating = stats.getAverage();
        this.name = doctor.getName();
        this.surname = doctor.getSurname();
        this.patronymic = doctor.getPatronymic();
        this.startWorkDate = doctor.getStartWorkDate();
        this.endWorkDate = doctor.getEndWorkDate();
        this.specializations = doctor.getSpecializations();
        this.isEnabled = doctor.isEnabled();
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
