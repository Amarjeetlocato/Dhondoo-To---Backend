package com.whoami.launch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.UserResponseDto;
import com.whoami.launch.feign.UserFeignClient;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserFeignClient userFeignClient;

    public AdminUserController(
            UserFeignClient userFeignClient
    ) {
        this.userFeignClient = userFeignClient;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {

        return userFeignClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(
            @PathVariable String userId
    ) {

        return userFeignClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(
            @PathVariable String userId
    ) {

        userFeignClient.deleteUser(userId);
    }
}