package com.whoami.launch.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whoami.launch.controller.UserResponse;
import com.whoami.launch.entity.User;
import com.whoami.launch.exception.UserNotFoundException;
import com.whoami.launch.repository.UserRepository;
import com.whoami.launch.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

	public Optional<User> getUserByUserId(String userId) {
		// TODO Auto-generated method stub
		return userRepository.findByUserId(userId);
	}

	
}
