package com.whoami.launch.repository;

import com.whoami.launch.entity.BusinessFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessFollowerRepository
        extends JpaRepository<BusinessFollower, Long> {

    Optional<BusinessFollower> findByBusinessIdAndUserId(
            String businessId,
            String userId);

    List<BusinessFollower> findByBusinessId(String businessId);

    List<BusinessFollower> findByUserId(String userId);

    Long countByBusinessId(String businessId);

    void deleteByBusinessIdAndUserId(
            String businessId,
            String userId);
}