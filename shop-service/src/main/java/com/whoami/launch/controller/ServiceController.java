package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ServiceSummaryDTO;
import com.whoami.launch.entity.Service;
import com.whoami.launch.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {

        return ResponseEntity.ok(
                serviceService.getAllServices());
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<Service> getServiceById(
            @PathVariable String serviceId) {

        Optional<Service> service =
                serviceService.getServiceById(serviceId);

        return service
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name/{serviceName}")
    public ResponseEntity<List<Service>> getServicesByName(
            @PathVariable String serviceName) {

        return ResponseEntity.ok(
                serviceService.getServicesByName(serviceName));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Service>> getServicesByBusinessId(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                serviceService.getServicesByBusinessId(businessId));
    }

    @GetMapping("/search/visibility/{visibility}")
    public ResponseEntity<List<Service>> getServicesByVisibility(
            @PathVariable String visibility) {

        return ResponseEntity.ok(
                serviceService.getServicesByVisibility(visibility));
    }

    @GetMapping("/search/badges/{badges}")
    public ResponseEntity<List<Service>> getServicesByBadges(
            @PathVariable String badges) {

        return ResponseEntity.ok(
                serviceService.getServicesByBadges(badges));
    }

    @GetMapping("/search/query")
    public ResponseEntity<List<Service>> searchServices(
            @RequestParam String query) {

        return ResponseEntity.ok(
                serviceService.searchServices(query));
    }

    @PostMapping
    public ResponseEntity<Service> createService(
            @RequestBody Service service) {

        Service createdService =
                serviceService.createService(service);

        return ResponseEntity.ok(createdService);
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<Service> updateService(
            @PathVariable String serviceId,
            @RequestBody Service serviceDetails) {

        Service updatedService =
                serviceService.updateService(
                        serviceId,
                        serviceDetails);

        if (updatedService != null) {
            return ResponseEntity.ok(updatedService);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(
            @PathVariable String serviceId) {

        serviceService.deleteService(serviceId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal-api/services/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>>
    getInternalServiceById(
            @PathVariable String serviceId) {

        Optional<Service> service =
                serviceService.getServiceById(serviceId);

        if (service.isPresent()) {

            ServiceResponseDTO dto =
                    serviceService.toResponseDTO(
                            service.get());

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Service retrieved",
                            dto));
        }

        return ResponseEntity.ok(
                ApiResponse.error("Service not found"));
    }

    @GetMapping("/internal-api/services/business/{businessId}")
    public ResponseEntity<ApiResponse<List<ServiceSummaryDTO>>>
    getInternalServicesByBusinessId(
            @PathVariable String businessId) {

        List<Service> services =
                serviceService.getServicesByBusinessId(
                        businessId);

        if (!services.isEmpty()) {

            List<ServiceSummaryDTO> dtos =
                    services.stream()
                            .map(serviceService::toSummaryDTO)
                            .toList();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Services retrieved",
                            dtos));
        }

        return ResponseEntity.ok(
                ApiResponse.error(
                        "No services found"));
    }

    @GetMapping("/internal-api/services/exists/{serviceId}")
    public ResponseEntity<ApiResponse<Boolean>>
    checkServiceExists(
            @PathVariable String serviceId) {

        Optional<Service> service =
                serviceService.getServiceById(serviceId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check completed",
                        service.isPresent()));
    }
}