package com.whoami.launch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

                     
	Optional<User> findByEmail(String email);
	Optional<User> findByUserId(String userId);
	boolean existsByUsername(String username);
	
	

	Optional<User> findByEmailOrUsername(String email, String username);
	Optional<User> findByUserIdAndDeletedFalse(String id);
	Optional<User> findByEmailAndDeletedFalse(String email);

	

	boolean existsByEmail(String email);

}
