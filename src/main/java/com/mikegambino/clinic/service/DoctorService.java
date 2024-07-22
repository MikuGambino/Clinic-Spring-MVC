package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.AccessDeniedException;
import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Review;
import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.User;
import com.mikegambino.clinic.model.dto.DoctorRequest;
import com.mikegambino.clinic.model.dto.DoctorResponse;
import com.mikegambino.clinic.model.enums.ReviewStatus;
import com.mikegambino.clinic.model.roles.RoleName;
import com.mikegambino.clinic.repository.DoctorRepository;
import com.mikegambino.clinic.repository.ReviewRepository;
import com.mikegambino.clinic.repository.SpecializationRepository;
import com.mikegambino.clinic.repository.UserRepository;
import com.mikegambino.clinic.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.IntSummaryStatistics;
import java.util.List;

import static com.mikegambino.clinic.util.AppConstants.*;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public List<DoctorResponse> getDoctorsBySpecialization(int specId) {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new ResourceNotFoundException(SPECIALIZATION, ID, specId));

        List<DoctorResponse> docs = specialization.getDoctors().stream()
                .map(d -> new DoctorResponse(d, getDoctorRating(d.getId())))
                .toList();

        return docs;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    private Doctor getDoctor(int id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DOCTOR, ID, id));
    }

    public boolean doctorExist(int id) {
        return doctorRepository.findById(id).isPresent();
    }

    public Doctor getDoctorOrNull(int id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public DoctorResponse getDoctorResponse (int id) {
        return new DoctorResponse(getDoctor(id), getDoctorRating(id));
    }

    public void addSpecialization(int docId, int specId) {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new ResourceNotFoundException(SPECIALIZATION, ID, specId));

        Doctor doctor = getDoctor(docId);
        doctor.getSpecializations().add(specialization);
        doctorRepository.save(doctor);
    }

    public DoctorRequest getDoctorRequest(int id) {
        Doctor doctor = getDoctor(id);

        DoctorRequest doctorRequest = DoctorRequest.builder()
                .name(doctor.getName())
                .surname(doctor.getSurname())
                .patronymic(doctor.getPatronymic())
                .description(doctor.getDescription())
                .isEnabled(doctor.isEnabled())
                .build();

        return doctorRequest;
    }

    public void removeSpecialization(int docId, int specId) {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new ResourceNotFoundException(SPECIALIZATION, ID, specId));

        Doctor doctor = getDoctor(docId);
        doctor.getSpecializations().remove(specialization);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void updateDoctor(int id, DoctorRequest doctorRequest, MultipartFile multipartFile, UserPrincipal currentUser) {
        Doctor doctor = getDoctor(id);

        if (!currentUser.getId().equals(doctor.getId()) &&
                !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()))) {
            throw new AccessDeniedException();
        }

        if(multipartFile != null && !multipartFile.isEmpty()) {
            String image = imageService.saveImage(multipartFile);
            doctor.setImage(image);
        }

        doctor.setName(doctorRequest.getName());
        doctor.setSurname(doctorRequest.getSurname());
        doctor.setPatronymic(doctorRequest.getPatronymic());
        doctor.setDescription(doctorRequest.getDescription());
        doctor.setEnabled(doctorRequest.getIsEnabled());
        doctorRepository.save(doctor);

        User user = doctor.getUser();
        user.setFullname(
                doctorRequest.getName(),
                doctorRequest.getSurname(),
                doctorRequest.getPatronymic()
        );
        userRepository.save(user);
    }

    public List<DoctorResponse> getDoctorsWithScheduleBySpecialization(int specId) {
        return doctorRepository.findDoctorsWithScheduleBySpecialization(specId).stream()
                .filter(Doctor::isEnabled)
                .map(d -> new DoctorResponse(d, getDoctorRating(d.getId())))
                .toList();
    }

    public IntSummaryStatistics getDoctorRating(int doctorId) {
        List<Review> reviews = reviewRepository.findReviewsByDoctorAndStatus(doctorId, ReviewStatus.ACCEPTED);
        return reviews.stream()
                .mapToInt(Review::getScore)
                .summaryStatistics();
    }

    public List<DoctorResponse> getDoctorsWithSchedule() {
        return doctorRepository.findDoctorsWithSchedule().stream()
                .filter(Doctor::isEnabled)
                .map(d -> new DoctorResponse(d, getDoctorRating(d.getId())))
                .toList();
    }

    public boolean isDoctorHasSchedule(int doctorId) {
        return doctorRepository.isDoctorHasSchedule(doctorId);
    }
}
