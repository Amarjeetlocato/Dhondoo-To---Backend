package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.whoami.launch.dto.NearbyProductDTO;
import com.whoami.launch.dto.NearbyReelDTO;
import com.whoami.launch.dto.NearbyServiceDTO;
import com.whoami.launch.dto.NearbyShopDTO;
import com.whoami.launch.entity.Location;
import com.whoami.launch.entity.Service;
import com.whoami.launch.entity.Shop;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.repository.ShopRepository;
import com.whoami.launch.util.DistanceCalculator;

@org.springframework.stereotype.Service
public class NearbyService {
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ReelRepository reelRepository;
    
    @Autowired
    private LocationService locationService;
    
    public List<NearbyShopDTO> findNearbyShops(String userId, Double radiusKm, int limit) {
        Optional<Location> userLocation = locationService.getLocationsByUserId(userId)
                .stream()
                .findFirst();
        
        if (userLocation.isEmpty() || userLocation.get().getLatitude() == null) {
            return List.of();
        }
        
        Location location = userLocation.get();
        Double userLat = location.getLatitude();
        Double userLon = location.getLongitude();
        
        return shopRepository.findAllWithCoordinates()
                .stream()
                .map(shop -> {
                    double distance = DistanceCalculator.calculateDistance(userLat, userLon, shop.getLatitude(), shop.getLongitude());
                    return new NearbyShopDTO(shop.getShopId(), shop.getShopName(), shop.getUserId(), 
                            shop.getAddress(), shop.getLatitude(), shop.getLongitude(), distance, shop.getMobileNumber());
                })
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<NearbyProductDTO> findNearbyProducts(String userId, Double radiusKm, int limit) {
        Optional<Location> userLocation = locationService.getLocationsByUserId(userId)
                .stream()
                .findFirst();
        
        if (userLocation.isEmpty() || userLocation.get().getLatitude() == null) {
            return List.of();
        }
        
        Location location = userLocation.get();
        Double userLat = location.getLatitude();
        Double userLon = location.getLongitude();
        
        return productRepository.findAllFromShopsWithCoordinates()
                .stream()
                .filter(product -> {
                    Optional<Shop> shop = shopRepository.findById(product.getShopId());
                    return shop.isPresent() && shop.get().getLatitude() != null && shop.get().getLongitude() != null;
                })
                .map(product -> {
                    Shop shop = shopRepository.findById(product.getShopId()).get();
                    double distance = DistanceCalculator.calculateDistance(userLat, userLon, shop.getLatitude(), shop.getLongitude());
                    return new NearbyProductDTO(product.getProductId(), product.getProductName(), product.getShopId(),
                            product.getProductPrice(), product.getProductDescription(), distance);
                })
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<NearbyServiceDTO> findNearbyServices(String userId, Double radiusKm, int limit) {
        Optional<Location> userLocation = locationService.getLocationsByUserId(userId)
                .stream()
                .findFirst();
        
        if (userLocation.isEmpty() || userLocation.get().getLatitude() == null) {
            return List.of();
        }
        
        Location location = userLocation.get();
        Double userLat = location.getLatitude();
        Double userLon = location.getLongitude();
        
        return serviceRepository.findAllFromShopsWithCoordinates()
                .stream()
                .filter(service -> {
                    Optional<Shop> shop = shopRepository.findById(service.getShopId());
                    return shop.isPresent() && shop.get().getLatitude() != null && shop.get().getLongitude() != null;
                })
                .map(service -> {
                    Shop shop = shopRepository.findById(service.getShopId()).get();
                    double distance = DistanceCalculator.calculateDistance(userLat, userLon, shop.getLatitude(), shop.getLongitude());
                    return new NearbyServiceDTO(service.getServiceId(), service.getServiceName(), service.getShopId(),
                            service.getPrice(), service.getServiceDescription(), distance);
                })
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<NearbyReelDTO> findNearbyReels(String userId, Double radiusKm, int limit) {
        Optional<Location> userLocation = locationService.getLocationsByUserId(userId)
                .stream()
                .findFirst();
        
        if (userLocation.isEmpty() || userLocation.get().getLatitude() == null) {
            return List.of();
        }
        
        Location location = userLocation.get();
        Double userLat = location.getLatitude();
        Double userLon = location.getLongitude();
        
        return reelRepository.findAllFromShopsWithCoordinates()
                .stream()
                .filter(reel -> {
                    Optional<Shop> shop = shopRepository.findById(reel.getShopId());
                    return shop.isPresent() && shop.get().getLatitude() != null && shop.get().getLongitude() != null;
                })
                .map(reel -> {
                    Shop shop = shopRepository.findById(reel.getShopId()).get();
                    double distance = DistanceCalculator.calculateDistance(userLat, userLon, shop.getLatitude(), shop.getLongitude());
                    return new NearbyReelDTO(reel.getReelId(), reel.getShopId(), reel.getReelVideo(), reel.getReelDescription(), distance);
                })
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
