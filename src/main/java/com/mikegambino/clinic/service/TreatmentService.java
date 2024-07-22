package com.mikegambino.clinic.service;

import com.mikegambino.clinic.exception.ResourceNotFoundException;
import com.mikegambino.clinic.model.Specialization;
import com.mikegambino.clinic.model.Treatment;
import com.mikegambino.clinic.model.dto.TreatmentRequest;
import com.mikegambino.clinic.repository.SpecializationRepository;
import com.mikegambino.clinic.repository.TreatmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mikegambino.clinic.util.AppConstants.*;

@Service
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final SpecializationRepository specializationRepository;

    public TreatmentService(TreatmentRepository treatmentRepository, SpecializationRepository specializationRepository) {
        this.treatmentRepository = treatmentRepository;
        this.specializationRepository = specializationRepository;
    }

    public void addTreatment(TreatmentRequest treatmentRequest, int specId) {
        Specialization specialization = specializationRepository.findById(specId)
                .orElseThrow(() -> new ResourceNotFoundException(SPECIALIZATION, ID, specId));

        Treatment treatment = Treatment.builder()
                .specialization(specialization)
                .title(treatmentRequest.getTitle())
                .description(treatmentRequest.getDescription())
                .price(treatmentRequest.getPrice())
                .beneficiaryDiscount(treatmentRequest.getBeneficiaryDiscount())
                .discount(treatmentRequest.getDiscount())
                .build();

        treatmentRepository.save(treatment);
    }

    public void updateTreatment(Integer id, TreatmentRequest treatmentRequest) {
        Treatment treatment = getTreatment(id);

        treatment.setTitle(treatmentRequest.getTitle());
        treatment.setDescription(treatmentRequest.getDescription());
        treatment.setPrice(treatmentRequest.getPrice());
        treatment.setBeneficiaryDiscount(treatmentRequest.getBeneficiaryDiscount());
        treatment.setDiscount(treatmentRequest.getDiscount());

        treatmentRepository.save(treatment);
    }

    public void deleteTreatment(int id) {
        Treatment treatment = getTreatment(id);
        treatmentRepository.delete(treatment);
    }

    public List<Treatment> getTreatmentsBySpecialization(int specId) {
        return treatmentRepository.findTreatmentsBySpecId(specId);
    }

    public TreatmentRequest getTreatmentRequest(int treatmentId) {
        Treatment treatment = getTreatment(treatmentId);

        TreatmentRequest treatmentRequest = new TreatmentRequest(
                treatment.getTitle(),
                treatment.getDescription(),
                treatment.getPrice(),
                treatment.getBeneficiaryDiscount(),
                treatment.getDiscount()
        );

        return treatmentRequest;
    }

    public Treatment getTreatment(Integer id) {
        return treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TREATMENT, ID, id));
    }
}
