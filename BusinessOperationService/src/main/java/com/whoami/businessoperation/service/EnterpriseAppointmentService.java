package com.whoami.businessoperation.service;

import com.whoami.businessoperation.dto.request.EnterpriseAppointmentRequest;

import java.util.Map;

public interface EnterpriseAppointmentService {

    Map<String, Object> createAppointment(EnterpriseAppointmentRequest request);
}
