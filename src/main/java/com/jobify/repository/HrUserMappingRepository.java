package com.jobify.repository;

import com.jobify.entities.HrUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HrUserMappingRepository extends JpaRepository<HrUserMapping, Long> {

    boolean existsByUser_IdAndHr_Id(Long userId, Long hrId);

    Optional<HrUserMapping> findByUser_IdAndHr_Id(Long userId, Long hrId);

    Optional<HrUserMapping> findByUser_IdAndHr_IdAndActiveTrue(Long userId, Long hrId);
}
