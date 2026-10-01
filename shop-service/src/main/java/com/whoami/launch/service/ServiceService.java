package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ServiceSummaryDTO;
import com.whoami.launch.entity.Service;

public interface ServiceService {

    Service createService(Service service);

    PageResponse<ServiceResponseDTO> getAllservices(
            Pageable pageable);

    Service updateService(
            String serviceId,
            Service serviceDetails);

    Optional<Service> getServiceById(
            String serviceId);

    List<Service> getServicesByName(
            String serviceName);

    List<Service> searchServices(
            String serviceName);

    List<Service> getServicesByBusinessId(
            String businessId);

    List<Service> getServicesByVisibility(
            String visibility);

    List<Service> getServicesByBadges(
            String badges);

    List<Service> getAllServices();

    void deleteService(
            String serviceId);

    ServiceResponseDTO toResponseDTO(
            Service service);

    ServiceSummaryDTO toSummaryDTO(
            Service service);
}