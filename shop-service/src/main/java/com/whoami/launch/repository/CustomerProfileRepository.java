package com.whoami.launch.repository;

import com.whoami.launch.entity.CustomerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository
        extends JpaRepository<CustomerProfile, Long> {

    Optional<CustomerProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);

    Optional<CustomerProfile> findByEmail(String email);

    Page<CustomerProfile> findAll(Pageable pageable);
}