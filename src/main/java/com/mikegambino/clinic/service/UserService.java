package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.BadRequestException;
import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Patient;
import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.User;
import com.mikegambino.clinic.model.dto.DoctorRequest;
import com.mikegambino.clinic.model.dto.forms.SignUpForm;
import com.mikegambino.clinic.model.roles.Role;
import com.mikegambino.clinic.model.roles.RoleName;
import com.mikegambino.clinic.repository.DoctorRepository;
import com.mikegambino.clinic.repository.PatientRepository;
import com.mikegambino.clinic.repository.RoleRepository;
import com.mikegambino.clinic.repository.UserRepository;
import com.mikegambino.clinic.security.UserPrincipal;
import static com.mikegambino.clinic.util.AppConstants.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    public UserService(UserRepository userRepository, DoctorRepository doctorRepository,
                       PatientRepository patientRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, ImageService imageService) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
    }


    public User getByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User getById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, id));

        return user;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void addUser(SignUpForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        User user = new User(form);

        List<Role> roles = new ArrayList<>();
        roles.add(
                roleRepository.findByTitle(RoleName.ROLE_PATIENT).orElseThrow(() -> new RuntimeException("User role not set"))
        );

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRegistrationDateTime(LocalDateTime.now());

        userRepository.save(user);
        Logger.getAnonymousLogger().info(user.getId().toString());

        Patient patient = Patient.builder()
                .id(user.getId())
                .beneficiary(false)
                .name(user.getName())
                .surname(user.getSurname())
                .patronymic(user.getPatronymic())
                .build();

        patientRepository.save(patient);
    }

    @Transactional
    public void updateUser(User userForm, UserPrincipal currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        user.setFullname(userForm.getName(), userForm.getSurname(), userForm.getPatronymic());
        user.setGender(userForm.getGender());
        user.setBirthday(userForm.getBirthday());

        userRepository.save(user);

        Optional<Patient> optionalPatient = patientRepository.findById(currentUser.getId());
        Optional<Doctor> optionalDoctor = doctorRepository.findById(currentUser.getId());

        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setName(userForm.getName());
            patient.setSurname(userForm.getSurname());
            patient.setPatronymic(userForm.getPatronymic());
            patientRepository.save(patient);
        }

        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            doctor.setName(userForm.getName());
            doctor.setSurname(userForm.getSurname());
            doctor.setPatronymic(userForm.getPatronymic());
            doctorRepository.save(doctor);
        }
    }

    public String checkRole(UserPrincipal user, String role) {
        if (user.getAuthorities().isEmpty()) {
            throw new RuntimeException("User id " + user.getId() + " has no roles");
        }
        if (role != null && RoleName.containsRole(role)) {
            if (user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.getBySimpleName(role).name()))) {
                return role;
            }
        }
        if (user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()))) {
            return RoleName.ROLE_ADMIN.getRoleName();
        }
        if (user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_DOCTOR.name()))) {
            return RoleName.ROLE_DOCTOR.getRoleName();
        }

        return RoleName.ROLE_PATIENT.getRoleName();
    }

    public List<User> getUsersByRole(String roleTitle) {
        if (!RoleName.containsRole(roleTitle)) {
            throw new ResourceNotFoundException(ROLE, TITLE, roleTitle);
        }

        return userRepository.findByRole("ROLE_" + roleTitle.toUpperCase());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void addPatientRole(int id) {
        User user = getById(id);
        Role role = roleRepository.findByTitle(RoleName.ROLE_PATIENT).orElseThrow(
                () -> new ResourceNotFoundException(ROLE, TITLE, RoleName.ROLE_PATIENT)
        );
        user.getRoles().add(role);

        if (patientRepository.findById(id).isEmpty()) {
            Patient patient = Patient.builder()
                    .id(id)
                    .name(user.getName())
                    .surname(user.getSurname())
                    .patronymic(user.getPatronymic())
                    .beneficiary(false)
                    .build();

            patientRepository.save(patient);
        }

        userRepository.save(user);
    }

    public void removePatientRole(int id) {
        User user = getById(id);
        Role role = new Role(RoleName.ROLE_PATIENT);
        user.getRoles().remove(role);

        userRepository.save(user);
    }

    @Transactional
    public void addDoctorRole(int id, DoctorRequest doctorRequest, MultipartFile multipartFile) {
        User user = getById(id);
        Role role = roleRepository.findByTitle(RoleName.ROLE_DOCTOR).orElseThrow(
                () -> new ResourceNotFoundException(ROLE, TITLE, RoleName.ROLE_DOCTOR)
        );
        user.getRoles().add(role);

        Optional<Doctor> oldDoctorOptional = doctorRepository.findById(id);

        LocalDate startDate = LocalDate.now();
        List<Specialization> specializations = new ArrayList<>();
        String image = DEFAULT_DOCTOR_IMG;
        if (oldDoctorOptional.isPresent()) {
            Doctor oldDoctor = oldDoctorOptional.get();
            startDate = oldDoctor.getStartWorkDate();
            image = oldDoctor.getImage();
            specializations = oldDoctor.getSpecializations();
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            image = imageService.saveImage(multipartFile);
        }

        Doctor newDoctor = Doctor.builder()
                                .id(id)
                                .name(doctorRequest.getName())
                                .surname(doctorRequest.getSurname())
                                .patronymic(doctorRequest.getPatronymic())
                                .isEnabled(doctorRequest.getIsEnabled())
                                .image(image)
                                .startWorkDate(startDate)
                                .description(doctorRequest.getDescription())
                                .endWorkDate(null)
                                .specializations(specializations)
                                .build();

        doctorRepository.save(newDoctor);
        userRepository.save(user);
    }

    @Transactional
    public void removeDoctorRole(int id) {
        User user = getById(id);
        Role role = new Role(RoleName.ROLE_DOCTOR);
        user.getRoles().remove(role);

        Doctor doctor = doctorRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("The user does not have the role of a doctor"));
        doctor.setEndWorkDate(LocalDate.now());
        doctor.setEnabled(false);

        userRepository.save(user);
        doctorRepository.save(doctor);
    }
}
