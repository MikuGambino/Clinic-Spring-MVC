package com.mikegambino.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;
    private String name;
    private String surname;
    private String patronymic;
    private String description;
    private String image;
    @Column(name = "start_work_date")
    private LocalDate startWorkDate;
    @Column(name = "end_work_date")
    private LocalDate endWorkDate;
    @Column(name = "is_enabled")
    private boolean isEnabled;

    @ManyToMany
    @JoinTable(
            name = "doctor_specialization",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    private List<Specialization> specializations;

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
}
