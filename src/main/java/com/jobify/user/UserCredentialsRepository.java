package com.jobify.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    boolean existsByUser_Id(Long userId);
}
