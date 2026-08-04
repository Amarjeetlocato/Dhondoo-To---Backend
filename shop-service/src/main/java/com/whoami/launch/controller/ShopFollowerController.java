package com.whoami.launch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.FollowShopRequest;
import com.whoami.launch.service.ShopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shop-follow")
@RequiredArgsConstructor
public class ShopFollowerController {

	
	@Autowired
    private  ShopService service;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(
            @RequestBody FollowShopRequest request) {

        return ResponseEntity.ok(
                service.followShop(
                        request.getShopId(),
                        request.getUserId()));
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollow(
            @RequestBody FollowShopRequest request) {

        return ResponseEntity.ok(
                service.unfollowShop(
                        request.getShopId(),
                        request.getUserId()));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(
            @RequestParam String shopId,
            @RequestParam String userId) {

        return ResponseEntity.ok(
                service.getFollowStatus(
                        shopId,
                        userId));
    }
}