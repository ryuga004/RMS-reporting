package com.rms.reporting.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u.jwtSecret from UserEntity u where u.id = :userId")
    Optional<String> findJwtSecretByUserId(Long userId);
}
