package com.whoami.launch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.launch.entity.Admin;

public interface AdminRepository
        extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmailOrUsername(
            String email,
            String username
    );

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}