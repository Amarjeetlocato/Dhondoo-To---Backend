package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.whoami.launch.dto.NearbyBusinessDTO;
import com.whoami.launch.dto.NearbyProductDTO;
import com.whoami.launch.dto.NearbyReelDTO;
import com.whoami.launch.dto.NearbyServiceDTO;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.Location;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.LocationService;
import com.whoami.launch.service.NearbyService;
import com.whoami.launch.util.DistanceCalculator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NearbyServiceImpl implements NearbyService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final ReelRepository reelRepository;
    private final LocationService locationService;

    // =========================================================
    // EXISTING USER-BASED METHODS
    // =========================================================

    @Override
    public List<NearbyBusinessDTO> findNearbyBusiness(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        return findNearbyBusiness(
                location.getLatitude(),
                location.getLongitude(),
                radiusKm,
                limit
        );
    }

    @Override
    public List<NearbyProductDTO> findNearbyProducts(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        return findNearbyProducts(
                location.getLatitude(),
                location.getLongitude(),
                radiusKm,
                limit
        );
    }

    @Override
    public List<NearbyServiceDTO> findNearbyServices(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        return findNearbyServices(
                location.getLatitude(),
                location.getLongitude(),
                radiusKm,
                limit
        );
    }

    @Override
    public List<NearbyReelDTO> findNearbyReels(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        return findNearbyReels(
                location.getLatitude(),
                location.getLongitude(),
                radiusKm,
                limit
        );
    }

    // =========================================================
    // ANONYMOUS / PUBLIC COORDINATE-BASED METHODS
    // =========================================================

    @Override
    public List<NearbyBusinessDTO> findNearbyBusiness(
            Double latitude,
            Double longitude,
            Double radiusKm,
            int limit) {

        if (!isValidCoordinates(latitude, longitude)) {
            return List.of();
        }

        double safeRadius = normalizeRadius(radiusKm);
        int safeLimit = normalizeLimit(limit);

        return businessRepository.findAllWithCoordinates()
                .stream()
                .map(business -> {

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    latitude,
                                    longitude,
                                    business.getLatitude(),
                                    business.getLongitude());

                    return new NearbyBusinessDTO(
                            business.getBusinessId(),
                            business.getBusinessName(),
                            business.getUserId(),
                            business.getAddress(),
                            business.getLatitude(),
                            business.getLongitude(),
                            distance,
                            business.getMobileNumber());
                })
                .filter(dto -> dto.getDistance() <= safeRadius)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }

    @Override
    public List<NearbyProductDTO> findNearbyProducts(
            Double latitude,
            Double longitude,
            Double radiusKm,
            int limit) {

        if (!isValidCoordinates(latitude, longitude)) {
            return List.of();
        }

        double safeRadius = normalizeRadius(radiusKm);
        int safeLimit = normalizeLimit(limit);

        return productRepository
                .findAllWithBusinessCoordinates()
                .stream()
                .map(product -> {

                    Business business =
                            businessRepository
                                    .findByBusinessId(
                                            product.getBusinessId())
                                    .orElse(null);

                    if (business == null
                            || business.getLatitude() == null
                            || business.getLongitude() == null) {

                        return null;
                    }

                    /*
                     * CLOSED + hideProductsWhenClosed = true
                     *
                     * Product should not appear in nearby
                     * discovery.
                     */
                    if (shouldHideProductsAndServices(business)) {
                        return null;
                    }

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    latitude,
                                    longitude,
                                    business.getLatitude(),
                                    business.getLongitude());

                    return new NearbyProductDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getBusinessId(),
                            product.getProductPrice(),
                            product.getProductDescription(),
                            distance);
                })
                .filter(java.util.Objects::nonNull)
                .filter(dto -> dto.getDistance() <= safeRadius)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }

    @Override
    public List<NearbyServiceDTO> findNearbyServices(
            Double latitude,
            Double longitude,
            Double radiusKm,
            int limit) {

        if (!isValidCoordinates(latitude, longitude)) {
            return List.of();
        }

        double safeRadius = normalizeRadius(radiusKm);
        int safeLimit = normalizeLimit(limit);

        return serviceRepository
                .findAllFromBusinessesWithCoordinates()
                .stream()
                .map(service -> {

                    Business business =
                            businessRepository
                                    .findByBusinessId(
                                            service.getBusinessId())
                                    .orElse(null);

                    if (business == null
                            || business.getLatitude() == null
                            || business.getLongitude() == null) {

                        return null;
                    }

                    /*
                     * CLOSED + hideProductsWhenClosed = true
                     *
                     * Service should not appear in nearby
                     * discovery.
                     */
                    if (shouldHideProductsAndServices(business)) {
                        return null;
                    }

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    latitude,
                                    longitude,
                                    business.getLatitude(),
                                    business.getLongitude());

                    return new NearbyServiceDTO(
                            service.getServiceId(),
                            service.getServiceName(),
                            service.getBusinessId(),
                            business.getBusinessName(),
                            service.getPrice(),
                            service.getServiceDescription(),
                            distance);
                })
                .filter(java.util.Objects::nonNull)
                .filter(dto -> dto.getDistance() <= safeRadius)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }

    @Override
    public List<NearbyReelDTO> findNearbyReels(
            Double latitude,
            Double longitude,
            Double radiusKm,
            int limit) {

        if (!isValidCoordinates(latitude, longitude)) {
            return List.of();
        }

        double safeRadius = normalizeRadius(radiusKm);
        int safeLimit = normalizeLimit(limit);

        /*
         * Reels remain visible even when the business is CLOSED.
         */
        return reelRepository
                .findAllFromBusinessesWithCoordinates()
                .stream()
                .map(reel -> {

                    if (reel.getBusinessId() == null) {
                        return null;
                    }

                    Business business =
                            businessRepository
                                    .findByBusinessId(
                                            reel.getBusinessId())
                                    .orElse(null);

                    if (business == null
                            || business.getLatitude() == null
                            || business.getLongitude() == null) {

                        return null;
                    }

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    latitude,
                                    longitude,
                                    business.getLatitude(),
                                    business.getLongitude());

                    return new NearbyReelDTO(
                            reel.getReelId(),
                            reel.getBusinessId(),
                            reel.getReelVideo(),
                            reel.getReelDescription(),
                            distance);
                })
                .filter(java.util.Objects::nonNull)
                .filter(dto -> dto.getDistance() <= safeRadius)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }

    // =========================================================
    // CLOSED BUSINESS VISIBILITY
    // =========================================================

    private boolean shouldHideProductsAndServices(
            Business business) {

        return business.getBusinessStatus()
                        == com.locato.enums.BusinessStatus.CLOSED
                && Boolean.TRUE.equals(
                        business.getHideProductsWhenClosed());
    }

    // =========================================================
    // VALIDATION / NORMALIZATION
    // =========================================================

    private boolean isValidCoordinates(
            Double latitude,
            Double longitude) {

        return latitude != null
                && longitude != null
                && latitude >= -90
                && latitude <= 90
                && longitude >= -180
                && longitude <= 180;
    }

    private double normalizeRadius(
            Double radiusKm) {

        if (radiusKm == null || radiusKm <= 0) {
            return 10.0;
        }

        return Math.min(radiusKm, 100.0);
    }

    private int normalizeLimit(
            int limit) {

        if (limit <= 0) {
            return 20;
        }

        return Math.min(limit, 100);
    }
}