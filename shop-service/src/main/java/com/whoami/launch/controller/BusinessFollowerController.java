package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.FollowBusinessRequest;
import com.whoami.launch.service.BusinessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business-follow")
public class BusinessFollowerController {

    private final BusinessService businessService;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(
            @RequestBody FollowBusinessRequest request) {

        return ResponseEntity.ok(
                businessService.followBusiness(
                        request.getBusinessId(),
                        request.getUserId()));
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollow(
            @RequestBody FollowBusinessRequest request) {

        return ResponseEntity.ok(
                businessService.unfollowBusiness(
                        request.getBusinessId(),
                        request.getUserId()));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(
            @RequestParam String businessId,
            @RequestParam String userId) {

        return ResponseEntity.ok(
                businessService.getFollowStatus(
                        businessId,
                        userId));
    }
}