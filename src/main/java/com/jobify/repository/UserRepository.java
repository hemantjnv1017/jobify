package com.jobify.repository;

import com.jobify.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.credentials
            WHERE u.email = :email AND u.deleted = false
            """)
    Optional<User> findActiveWithCredentialsByEmail(@Param("email") String email);
}
