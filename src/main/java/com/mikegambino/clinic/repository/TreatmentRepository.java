package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Treatment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TreatmentRepository extends ListCrudRepository<Treatment, Integer> {

    @Query("""
            select t
            from Treatment t
            where t.specialization.id = :specId
            """)
    List<Treatment> findTreatmentsBySpecId(int specId);
}
