package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.dto.request.EnterpriseAppointmentRequest;
import com.whoami.businessoperation.service.EnterpriseAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/business-operations/enterprise-appointments")
@RequiredArgsConstructor
public class EnterpriseAppointmentController {

    private final EnterpriseAppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAppointment(
            @Valid @RequestBody EnterpriseAppointmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(request));
    }
}
