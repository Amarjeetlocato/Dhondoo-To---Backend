package com.whoami.launch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.whoami.launch.entity.AdminAuditLog;
import com.whoami.launch.repository.AdminAuditLogRepository;

@RestController
@RequestMapping("/admin/audit")
public class AuditController {

    private final AdminAuditLogRepository repository;

    public AuditController(
            AdminAuditLogRepository repository) {

        this.repository = repository;
    }

    @GetMapping
    public List<AdminAuditLog> logs() {

        return repository.findAll();
    }
}