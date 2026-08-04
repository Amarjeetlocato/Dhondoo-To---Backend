package com.whoami.launch.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.CustomerProfile;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, String> {
    Optional<CustomerProfile> findByUserId(String userId);
    boolean existsByUserId(String userId);
    Optional<CustomerProfile> findByUsername(String username);
    Optional<CustomerProfile> findByEmail(String email);
    Page<CustomerProfile> findAll(Pageable pageable);
    
}
