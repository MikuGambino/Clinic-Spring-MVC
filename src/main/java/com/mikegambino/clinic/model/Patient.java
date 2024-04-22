package com.mikegambino.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;
    private String name;
    private String surname;
    private String patronymic;
    private boolean beneficiary;
    @Column(name = "rh_factor")
    private Boolean rhFactor;
    @Column(name = "blood_type")
    private String bloodType;

    @OneToMany(mappedBy = "patient")
    private List<Review> reviews;

    public String getFullname() {
        if (patronymic == null) {
            return "%s %s".formatted(surname, name);
        }
        return "%s %s %s".formatted(surname, name, patronymic);
    }

}
