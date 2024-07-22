package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Patient;
import com.mikegambino.clinic.repository.PatientRepository;
import com.mikegambino.clinic.security.UserPrincipal;
import org.springframework.stereotype.Service;

import static com.mikegambino.clinic.util.AppConstants.ID;
import static com.mikegambino.clinic.util.AppConstants.PATIENT;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient getPatient(UserPrincipal currentUser) {
        Patient patient = patientRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT, ID, currentUser.getId()));

        return patient;
    }

    public Patient getPatientOrNull(int id) {
        return patientRepository.findById(id).orElse(null);
    }
}
