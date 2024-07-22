package com.mikegambino.clinic.repository;

import com.mikegambino.clinic.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<User, Integer> {

    @Query("""
            select u
            from users u
            where u.username = :username
            """)
    User findByUsername(String username);
    @Query("""
            select case when (count(*) > 0) then true else false end
            from users u
            where u.username = :username
            """)
    Boolean existsByUsername(String username);
    @Query("""
            select case when (count(*) > 0) then true else false end
            from users u
            where u.email = :email
            """)
    Boolean existsByEmail(String email);

    @Query(value = """
            select u.*
            from user_role ur
            join users u on ur.user_id = u.id
            join roles r on ur.role_id = r.id
            where r.title = :role
            """, nativeQuery = true)
    List<User> findByRole(String role);

}
