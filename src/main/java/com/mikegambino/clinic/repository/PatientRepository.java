package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface PatientRepository extends ListCrudRepository<Patient, Integer> {

    @Query("""
            select p
            from Patient p
            join users u on u = p.user
            where u.username = :username
            """)
    Optional<Patient> findPatientByUsername(String username);

}
