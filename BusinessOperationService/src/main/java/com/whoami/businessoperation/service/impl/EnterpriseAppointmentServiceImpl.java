package com.whoami.businessoperation.service.impl;

import com.whoami.businessoperation.domain.entity.EnterpriseAppointment;
import com.whoami.businessoperation.dto.request.EnterpriseAppointmentRequest;
import com.whoami.businessoperation.repository.EnterpriseAppointmentRepository;
import com.whoami.businessoperation.service.EnterpriseAppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EnterpriseAppointmentServiceImpl implements EnterpriseAppointmentService {

    private final EnterpriseAppointmentRepository repository;

    @Override
    public Map<String, Object> createAppointment(EnterpriseAppointmentRequest request) {
        log.info("Creating enterprise appointment request for businessId={}, name={}",
                request.getBusinessId(), request.getContactPersonName());

        EnterpriseAppointment appointment = EnterpriseAppointment.builder()
                .businessId(request.getBusinessId())
                .businessName(request.getBusinessName())
                .contactPersonName(request.getContactPersonName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .preferredDateTime(request.getPreferredDateTime())
                .notes(request.getNotes())
                .status("PENDING")
                .build();

        EnterpriseAppointment saved = repository.save(appointment);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("appointmentId", saved.getAppointmentId());
        response.put("businessId", saved.getBusinessId());
        response.put("businessName", saved.getBusinessName());
        response.put("contactPersonName", saved.getContactPersonName());
        response.put("phone", saved.getPhone());
        response.put("email", saved.getEmail());
        response.put("preferredDateTime", saved.getPreferredDateTime());
        response.put("status", saved.getStatus());
        response.put("createdAt", saved.getCreatedAt());
        response.put("message", "Enterprise appointment request submitted successfully. Our enterprise team will contact you shortly.");

        return response;
    }
}
