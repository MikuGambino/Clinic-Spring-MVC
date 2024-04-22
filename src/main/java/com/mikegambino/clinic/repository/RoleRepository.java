package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.roles.Role;
import com.mikegambino.clinic.model.roles.RoleName;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface RoleRepository extends ListCrudRepository<Role, Integer> {
    Optional<Role> findByTitle(RoleName title);

}
