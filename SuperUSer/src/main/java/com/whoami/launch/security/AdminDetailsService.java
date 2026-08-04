package com.whoami.launch.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.whoami.launch.entity.Admin;
import com.whoami.launch.repository.AdminRepository;

@Service
public class AdminDetailsService
        implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(
            AdminRepository adminRepository) {

        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        Admin admin =
                adminRepository
                        .findByEmailOrUsername(
                                username,
                                username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Admin not found"));

        return new AdminDetails(admin);
    }
}