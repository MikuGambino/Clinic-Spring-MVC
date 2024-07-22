package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.AccessDeniedException;
import com.mikegambino.clinic.exception.BadRequestException;
import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Appointment;
import com.mikegambino.clinic.model.Doctor;
import com.mikegambino.clinic.model.Patient;
import com.mikegambino.clinic.model.Review;
import com.mikegambino.clinic.model.dto.forms.ReviewForm;
import com.mikegambino.clinic.model.enums.ReviewStatus;
import com.mikegambino.clinic.model.roles.RoleName;
import com.mikegambino.clinic.repository.AppointmentRepository;
import com.mikegambino.clinic.repository.DoctorRepository;
import com.mikegambino.clinic.repository.PatientRepository;
import com.mikegambino.clinic.repository.ReviewRepository;
import com.mikegambino.clinic.security.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mikegambino.clinic.util.AppConstants.*;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public ReviewService(ReviewRepository reviewRepository, PatientRepository patientRepository,
                         AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.reviewRepository = reviewRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Review> getAcceptedReviewsByDoctor(int doctorId) {
        return reviewRepository.findReviewsByDoctorAndStatus(doctorId, ReviewStatus.ACCEPTED);
    }

    public List<Review> getModeratedReviews() {
        return reviewRepository.findReviewsByStatus(ReviewStatus.MODERATED);
    }

    public boolean canAddReview(int doctorId, UserPrincipal currentUser) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(DOCTOR, ID, doctorId));
        Patient patient = patientRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT, ID, currentUser.getId()));

        if (doctor.getId().equals(patient.getId())) {
            return false;
        }

        Optional<Review> review = reviewRepository.findReviewByDoctorAndPatient(doctorId, patient.getId());
        if (review.isPresent()) {
            return false;
        }

        List<Appointment> appointments = appointmentRepository.findByDoctorAndPatient(doctorId, patient.getId());
        if (appointments.isEmpty()) {
            return false;
        }

        return true;
    }

    public Review getModeratedReview(int doctorId, UserPrincipal currentUser) {
        Patient patient = patientRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT, ID, currentUser.getId()));

        Review review = reviewRepository.findReviewByDoctorAndPatient(doctorId, patient.getId()).orElse(null);
        if (review == null || review.getReviewStatus().equals(ReviewStatus.ACCEPTED)) {
            return null;
        }

        return review;
    }

    public void addReview(ReviewForm reviewForm, int doctorId, UserPrincipal currentUser) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(DOCTOR, ID, doctorId));
        Patient patient = patientRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT, ID, currentUser.getId()));
        List<Appointment> appointments = appointmentRepository.findByDoctorAndPatient(doctorId, patient.getId());
        if (appointments.isEmpty()) {
            throw new BadRequestException("Patient has no appointments");
        }

        Review review = Review.builder()
                .doctor(doctor)
                .patient(patient)
                .callDateTime(LocalDateTime.now())
                .appointmentDate(appointments.get(0).getStartDateTime().toLocalDate())
                .score(reviewForm.getScore())
                .description(reviewForm.getDescription())
                .reviewStatus(ReviewStatus.MODERATED).build();

        reviewRepository.save(review);
    }

    private Review getReview(int id, UserPrincipal currentUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REVIEW, ID, id));

        if (!review.getPatient().getId().equals(currentUser.getId()) &&
        !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()))) {
            throw new AccessDeniedException();
        }

        return review;
    }

    public void deleteReview(int id, UserPrincipal currentUser) {
        Review review = getReview(id, currentUser);
        reviewRepository.delete(review);
    }

    public void acceptReview(int id, UserPrincipal currentUser) {
        Review review = getReview(id, currentUser);
        reviewRepository.save(review);
    }
}
