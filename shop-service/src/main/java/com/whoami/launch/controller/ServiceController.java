package com.whoami.launch.controller;

import com.whoami.launch.entity.Service;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ServiceSummaryDTO;
import com.whoami.launch.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    
    @Autowired
    private ServiceService serviceService;
    
    // GET all services
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }
    
    // GET service by ID
    @GetMapping("/{serviceId}")
    public ResponseEntity<Optional<Service>> getServiceById(@PathVariable String serviceId) {
        Optional<Service> service = serviceService.getServiceById(serviceId);
        if (service.isPresent()) {
            return ResponseEntity.ok(service);
        }
        return ResponseEntity.notFound().build();
    }
    
    // GET services by name
    @GetMapping("/search/name/{serviceName}")
    public ResponseEntity<List<Service>> getServicesByName(@PathVariable String serviceName) {
        List<Service> services = serviceService.getServicesByName(serviceName);
        return ResponseEntity.ok(services);
    }
    
    // GET services by shop ID
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Service>> getServicesByShopId(@PathVariable String shopId) {
        List<Service> services = serviceService.getServicesByShopId(shopId);
        return ResponseEntity.ok(services);
    }
    
    // GET services by visibility
    @GetMapping("/search/visibility/{visibility}")
    public ResponseEntity<List<Service>> getServicesByVisibility(@PathVariable String visibility) {
        List<Service> services = serviceService.getServicesByVisibility(visibility);
        return ResponseEntity.ok(services);
    }
    
    // GET services by badges
    @GetMapping("/search/badges/{badges}")
    public ResponseEntity<List<Service>> getServicesByBadges(@PathVariable String badges) {
        List<Service> services = serviceService.getServicesByBadges(badges);
        return ResponseEntity.ok(services);
    }
    
    // GET services by search query
    @GetMapping("/search/query")
    public ResponseEntity<List<Service>> searchServices(@RequestParam String query) {
        List<Service> services = serviceService.searchServices(query);
        return ResponseEntity.ok(services);
    }
    
    // POST create new service
    @PostMapping("/create")
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service createdService = serviceService.createService(service);
        return ResponseEntity.ok(createdService);
    }
    
    // PUT update service
    @PutMapping("/{serviceId}")
    public ResponseEntity<Service> updateService(@PathVariable String serviceId, @RequestBody Service serviceDetails) {
        Service updatedService = serviceService.updateService(serviceId, serviceDetails);
        if (updatedService != null) {
            return ResponseEntity.ok(updatedService);
        }
        return ResponseEntity.notFound().build();
    }
    
    // DELETE service
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable String serviceId) {
        serviceService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }
    
    // Internal API endpoints for Feign clients
    @GetMapping("/internal-api/services/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> getInternalServiceById(@PathVariable String serviceId) {
        Optional<Service> service = serviceService.getServiceById(serviceId);
        if (service.isPresent()) {
            ServiceResponseDTO dto = serviceService.toResponseDTO(service.get());
            return ResponseEntity.ok(ApiResponse.success("Service retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.error("Service not found"));
    }
    
    @GetMapping("/internal-api/services/shop/{shopId}")
    public ResponseEntity<ApiResponse<List<ServiceSummaryDTO>>> getInternalServicesByShopId(@PathVariable String shopId) {
        List<Service> services = serviceService.getServicesByShopId(shopId);
        if (!services.isEmpty()) {
            List<ServiceSummaryDTO> dtos = services.stream().map(serviceService::toSummaryDTO).toList();
            return ResponseEntity.ok(ApiResponse.success("Services retrieved", dtos));
        }
        return ResponseEntity.ok(ApiResponse.error("No services found"));
    }
    
    @GetMapping("/internal-api/services/exists/{serviceId}")
    public ResponseEntity<ApiResponse<Boolean>> checkServiceExists(@PathVariable String serviceId) {
        Optional<Service> service = serviceService.getServiceById(serviceId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", service.isPresent()));
    }
}
