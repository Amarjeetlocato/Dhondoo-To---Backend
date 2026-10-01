package com.whoami.launch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entities.Business;

@Repository
public interface BusinessRepository
        extends JpaRepository<Business, Long> {
}