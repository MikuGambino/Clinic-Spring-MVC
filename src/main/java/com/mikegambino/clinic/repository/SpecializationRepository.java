package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.Specialization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface SpecializationRepository extends ListCrudRepository<Specialization, Integer> {

    @Query(value = """
            select *
            from specialization s1
            except
            select s2.id, s2.title, s2.description
            from doctor_specialization ds
            join specialization s2 on s2.id = ds.specialization_id
            where ds.doctor_id = :doctorId
            """, nativeQuery = true)
    List<Specialization> findFreeSpecializationsByDoctor(int doctorId);

    @Query(value = """
            select *
            from specialization s
            where s.id in
            (
            	select distinct t.specialization_id from treatment t 	
            )
            and s.id in
            (
            	select distinct ds.specialization_id from doctor_specialization ds
            )
            order by s.title
            """, nativeQuery = true)
    List<Specialization> findUsedSpecializations();
}
