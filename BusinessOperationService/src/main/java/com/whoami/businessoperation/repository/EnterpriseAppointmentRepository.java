package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.EnterpriseAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnterpriseAppointmentRepository extends JpaRepository<EnterpriseAppointment, Long> {

    Optional<EnterpriseAppointment> findByAppointmentId(String appointmentId);

    List<EnterpriseAppointment> findByBusinessId(String businessId);
}
