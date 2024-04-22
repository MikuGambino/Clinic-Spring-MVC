package com.mikegambino.clinic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    @OneToMany(mappedBy = "specialization")
    private List<Treatment> treatments;

    @ManyToMany
    @JoinTable(
            name = "doctor_specialization",
            joinColumns = @JoinColumn(name = "specialization_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    private Set<Doctor> doctors;

    public String getFormattedTreatments() {
        return treatments.stream()
                .map(Treatment::getTitle)
                .map(String::toLowerCase)
                .reduce((a, b) -> a + ", " + b)
                .orElse("услуг нет");
    }
}
