package com.whoami.launch.repository;

import com.whoami.launch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndDeletedFalse(String userId);

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);
}