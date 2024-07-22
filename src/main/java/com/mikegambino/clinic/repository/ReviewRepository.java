package com.mikegambino.clinic.repository;


import com.mikegambino.clinic.model.Review;
import com.mikegambino.clinic.model.enums.ReviewStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends ListCrudRepository<Review, Integer> {

    @Query("""
            select r
            from Review r
            where r.doctor.id = :doctorId
            and r.reviewStatus = :status
            """)
    List<Review> findReviewsByDoctorAndStatus(int doctorId, ReviewStatus status);

    @Query("""
            select r
            from Review r
            where r.doctor.id = :doctorId and r.patient.id = :patientId
            """)
    Optional<Review> findReviewByDoctorAndPatient(@Param("doctorId") int doctorId, @Param("patientId") int patientId);

    @Query("""
            select r
            from Review r
            where r.reviewStatus = :status
            """)
    List<Review> findReviewsByStatus(ReviewStatus status);

}
