package com.whoami.launch.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.whoami.launch.entity.User;
import com.whoami.launch.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("🔍 [DEBUG] loadUserByUsername called");
        System.out.println("👉 Email received: " + email);

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            System.out.println("❌ User NOT FOUND in DB");
            throw new UsernameNotFoundException("User not found");
        }

        User user = optionalUser.get();

        System.out.println("✅ User FOUND in DB");
        System.out.println("👉 DB Email: " + user.getEmail());
        System.out.println("👉 DB Password (encoded): " + user.getPassword());

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();

        System.out.println("✅ UserDetails created");
        System.out.println("👉 Username in UserDetails: " + userDetails.getUsername());
        System.out.println("👉 Password in UserDetails: " + userDetails.getPassword());

        return userDetails;
    }
}
