package com.whoami.launch.service;

import java.util.Optional;

import com.whoami.launch.entity.User;

public interface UserService {
    
    User getUserByEmail(String email);
    
    User saveUser(User user);
    
    User updateUser(User user);
    
    boolean userExists(String email);

	static Optional<User> getUserByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}