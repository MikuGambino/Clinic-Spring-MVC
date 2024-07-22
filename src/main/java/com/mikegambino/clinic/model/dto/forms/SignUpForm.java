package com.mikegambino.clinic.model.dto.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpForm {
	@NotBlank
	@Size(min = 4, max = 40)
	private String name;
	@NotBlank
	@Size(min = 4, max = 40)
	private String surname;
	private String patronymic;
	@NotBlank
	@Size(min = 3, max = 40)
	private String username;
	@NotBlank
	@Size(max = 60)
	private String email;
	@NotBlank
	@Size(min = 6, max = 40)
	private String password;
	@NotNull
	private LocalDate birthday;
	@NotNull
	private String gender;
}