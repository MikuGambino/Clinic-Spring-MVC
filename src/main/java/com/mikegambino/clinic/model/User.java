package com.mikegambino.clinic.model;

import com.mikegambino.clinic.model.dto.forms.SignUpForm;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.mikegambino.clinic.model.roles.Role;

@Data
@Entity(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    private String patronymic;
    private String password;
    private String username;
    private String email;
    private LocalDate birthday;
    @Column(name = "registration_timestamp")
    private LocalDateTime registrationDateTime;
    private String gender;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    public String getFullname() {
        if (patronymic == null) {
            return "%s %s".formatted(surname, name);
        }
        return "%s %s %s".formatted(surname, name, patronymic);
    }

    public String getFormattedBirthday() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTimeFormatter.format(birthday);
    }

    public String getFormattedRegistrationDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTimeFormatter.format(registrationDateTime);
    }

    public String getFormattedBirthdayRU() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTimeFormatter.format(birthday);
    }

    public void setFullname(String name, String surname, String patronymic) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
    }

    public String getFormattedRoles() {
        return roles.stream().map(r -> r.getTitle().getRoleName())
                .collect(Collectors.joining(", "));
    }

    public User(SignUpForm signUpForm) {
        this.name = signUpForm.getName();
        this.surname = signUpForm.getSurname();
        this.patronymic = signUpForm.getPatronymic();
        this.password = signUpForm.getPassword();
        this.username = signUpForm.getUsername().toLowerCase();
        this.email = signUpForm.getEmail();
        this.birthday = signUpForm.getBirthday();
        this.gender = signUpForm.getGender();
    }
}
