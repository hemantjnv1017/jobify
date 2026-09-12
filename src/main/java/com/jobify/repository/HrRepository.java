package com.jobify.repository;

import com.jobify.entities.Hr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HrRepository extends JpaRepository<Hr, Long> {

    boolean existsByEmail(String email);

    Optional<Hr> findByEmail(String email);
}
