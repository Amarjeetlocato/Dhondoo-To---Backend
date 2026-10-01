package com.whoami.launch.service;

import com.whoami.launch.entity.User;

import java.util.Optional;

public interface UserService {

    User getUserByEmail(String email);

    Optional<User> getUserByUserId(String userId);

    User saveUser(User user);

    User updateUser(User user);

    boolean userExists(String email);
}