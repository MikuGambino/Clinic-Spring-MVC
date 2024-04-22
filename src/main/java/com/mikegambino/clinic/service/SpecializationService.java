package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.dto.SpecializationRequest;
import com.mikegambino.clinic.repository.SpecializationRepository;
import static com.mikegambino.clinic.util.AppConstants.*;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public void updateSpecialization(int id, SpecializationRequest specializationRequest) {
        Specialization specialization = getSpecialization(id);

        specialization.setTitle(specializationRequest.getTitle());
        specialization.setDescription(specializationRequest.getDescription());

        specializationRepository.save(specialization);
    }

    public Specialization addSpecialization(SpecializationRequest specializationRequest) {
        Specialization specialization = new Specialization();
        specialization.setTitle(specializationRequest.getTitle());
        specialization.setDescription(specializationRequest.getDescription());

        specializationRepository.save(specialization);

        return specialization;
    }

    public Specialization getSpecialization(int id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SPECIALIZATION, ID, id));
    }

    public SpecializationRequest getSpecializationRequest(int id) {
        Specialization specialization = getSpecialization(id);

        SpecializationRequest specializationRequest = new SpecializationRequest();
        specializationRequest.setDescription(specialization.getDescription());
        specializationRequest.setTitle(specialization.getTitle());

        return specializationRequest;
    }

    public void deleteSpecialization(int id) {
        Specialization specialization = getSpecialization(id);
        specializationRepository.delete(specialization);
    }

    public List<Specialization> getFreeSpecializationsByDoctor(int docId) {
        return specializationRepository.findFreeSpecializationsByDoctor(docId);
    }

    public List<Specialization> getUsedSpecializations() {
        return specializationRepository.findUsedSpecializations();
    }
}
