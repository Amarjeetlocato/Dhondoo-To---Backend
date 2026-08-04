package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.dto.ShopCreatedEvent;
import com.locato.dto.ShopDeletedEvent;
import com.locato.dto.ShopStatusChangedEvent;
import com.locato.dto.ShopUpdatedEvent;
import com.whoami.launch.dto.FollowShopResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.dto.ShopSummaryDTO;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.entity.Shop;
import com.whoami.launch.entity.ShopFollower;
import com.locato.enums.ShopStatus;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.producer.ShopKafkaProducer;
import com.whoami.launch.repository.CustomerProfileRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.repository.ShopFollowerRepository;
import com.whoami.launch.repository.ShopRepository;
import com.whoami.launch.util.SlugUtil;
import com.locato.topics.KafkaTopics;

import jakarta.transaction.Transactional;

@Service
public class ShopService {

	@Autowired
	private ShopRepository shopRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ReelRepository reelRepository;

	@Autowired
	private ServiceRepository serviceRepository;


	@Autowired
	private CustomerProfileRepository customerProfileRepository;
	
	
	
	@Autowired
	private ShopKafkaProducer shopKafkaProducer;


	@Autowired
	private ShopFollowerRepository followerRepository;

	public Shop createShop(Shop shop) {

	    System.out.println("========== CREATE SHOP START ==========");

	    try {

	        System.out.println("Incoming Shop : " + shop);

	        // Step 1
	        System.out.println("Checking mobile number...");
	        if (shopRepository.findByMobileNumber(shop.getMobileNumber()).isPresent()) {
	            System.out.println("Shop already exists!");
	            throw new RuntimeException(
	                    "Shop with mobile number " + shop.getMobileNumber() + " already exists");
	        }

	        // Step 2
	        System.out.println("Generating slug...");
	        String slug;
	        do {
	            slug = SlugUtil.generate(shop.getShopName());
	            System.out.println("Generated slug : " + slug);
	        } while (shopRepository.existsBySlug(slug));

	        shop.setSlug(slug);

	        // Step 3
	        System.out.println("Saving shop...");
	        Shop savedShop = shopRepository.save(shop);
	        System.out.println("Shop saved successfully.");
	        System.out.println("Shop ID : " + savedShop.getShopId());

	        // Step 4
	        System.out.println("Creating ShopCreatedEvent...");
	        
	        CustomerProfile profile =
			        customerProfileRepository
			                .findByUserId(shop.getUserId())
			                .orElse(null);


			String shopLogo =
			        profile != null
			                ? profile.getLogoUrl()
			                : null;

			String shopBanner =
			        profile != null
			                ? profile.getBannerUrl()
			                : null;

	        ShopCreatedEvent event = ShopCreatedEvent.builder()
	                .eventId(UUID.randomUUID().toString())
	                .eventType("SHOP_CREATED")
	                .eventTime(LocalDateTime.now())

	                .shopId(savedShop.getShopId())
	                .userId(savedShop.getUserId())

	                .shopName(savedShop.getShopName())
//	                .mobileNumber(savedShop.getMobileNumber())
//	                .slug(savedShop.getSlug())

	                .logoUrl(profile.getLogoUrl())
	                .bannerUrl(profile.getBannerUrl())

	                .build();
	        System.out.println("Event Created : " + event);

	        // Step 5
	        System.out.println("Publishing event to Kafka...");
	        shopKafkaProducer.publish(
	                KafkaTopics.SHOP_EVENTS,
	                event
	        );
	        System.out.println("Kafka publish completed.");

	        System.out.println("========== CREATE SHOP SUCCESS ==========");

	        return savedShop;

	    } catch (Exception e) {

	        System.out.println("========== CREATE SHOP FAILED ==========");
	        e.printStackTrace();

	        throw e;
	    }
	}
	public Shop updateShop(String shopId, Shop shopDetails) {

		Optional<Shop> shop = shopRepository.findById(shopId);

		if (shop.isPresent()) {

			Shop existingShop = shop.get();

			StringBuilder changes = new StringBuilder();

			if (shopDetails.getShopName() != null) {
			    existingShop.setShopName(shopDetails.getShopName());
			    changes.append("Shop name updated. ");
			}

			if (shopDetails.getAddress() != null) {
			    existingShop.setAddress(shopDetails.getAddress());
			    changes.append("Address updated. ");
			}

			if (shopDetails.getMobileNumber() != null) {
			    existingShop.setMobileNumber(shopDetails.getMobileNumber());
			    changes.append("Mobile updated. ");
			}

			if (shopDetails.getLatitude() != null) {
			    existingShop.setLatitude(shopDetails.getLatitude());
			    changes.append("Location updated. ");
			}

			if (shopDetails.getLongitude() != null) {
			    existingShop.setLongitude(shopDetails.getLongitude());
			    changes.append("Location updated. ");
			}

			if (shopDetails.getShopStatus() != null) {
			    existingShop.setShopStatus(shopDetails.getShopStatus());
			    changes.append("Status updated. ");
			}
			Shop updatedShop = shopRepository.save(existingShop);

			ShopUpdatedEvent event = ShopUpdatedEvent.builder()
			        .eventId(UUID.randomUUID().toString())
			        .eventType("SHOP_UPDATED")
			        .eventTime(LocalDateTime.now())

			        .shopId(updatedShop.getShopId())
			        .userId(updatedShop.getUserId())

			        .shopName(updatedShop.getShopName())
			        .mobileNumber(updatedShop.getMobileNumber())
			        .slug(updatedShop.getSlug())

			        .changes(changes.toString())

			        .build();
			shopKafkaProducer.publish(
			        KafkaTopics.SHOP_EVENTS,
			        event
			);
		}

		return null;
	}

	public Optional<Shop> getShopById(String shopId) {
		return shopRepository.findById(shopId);
	}

	public Optional<Shop> getShopByName(String shopName) {
		return shopRepository.findByShopName(shopName);
	}

	public List<Shop> getShopsByUserId(String userId) {
		return shopRepository.findByUserId(userId);
	}

	public List<Shop> searchShops(String shopName) {
		return shopRepository.findByShopNameContaining(shopName);
	}

	public List<Shop> getAllShops() {
		return shopRepository.findAll();
	}

	public void deleteShop(String shopId) {

		Optional<Shop> shop = shopRepository.findById(shopId);

		shop.ifPresent(existingShop -> {

		    shopRepository.deleteById(existingShop.getShopId());

		    ShopDeletedEvent event =
		            ShopDeletedEvent.builder()
		                    .eventId(UUID.randomUUID().toString())
		                    .eventType("SHOP_DELETED")
		                    .eventTime(LocalDateTime.now())

		                    .shopId(existingShop.getShopId())
		                    .userId(existingShop.getUserId())
		                    .shopName(existingShop.getShopName())

		                    .build();

		    shopKafkaProducer.publish(
		            KafkaTopics.SHOP_EVENTS,
		            event
		    );
		});
		shopRepository.deleteById(shopId);
	}

	public ShopResponseDTO toResponseDTO(Shop shop) {

		if (shop == null) {
			return null;
		}

		long totalProducts = productRepository.countByShopId(shop.getShopId());

		long totalReels = reelRepository.countByShopId(shop.getShopId());

		long totalServices = serviceRepository.countByShopId(shop.getShopId());

		return new ShopResponseDTO(shop.getShopId(), shop.getUserId(),

				shop.getShopName(), shop.getMobileNumber(),

				shop.getAddress(), shop.getVillage(), shop.getBlock(), shop.getDistrict(), shop.getState(),
				shop.getCountry(), shop.getPincode(),

				shop.getLatitude(), shop.getLongitude(),

				totalProducts, totalReels, totalServices,

				shop.getShopStatus(),

				shop.getAcceptingOrders(), shop.getAutoMode(),

				shop.getOpeningTime(), shop.getClosingTime());
	}

	public ShopSummaryDTO toSummaryDTO(Shop shop) {

		if (shop == null) {
			return null;
		}

		return new ShopSummaryDTO(shop.getShopId(), shop.getShopName(), shop.getUserId(),

				shop.getVillage(), shop.getDistrict(), shop.getPincode(),

				shop.getLatitude(), shop.getLongitude(),

				shop.getAddress(), shop.getMobileNumber());
	}


	public PageResponse<ShopResponseDTO> getAllShops(Pageable pageable) {

		Page<Shop> page = shopRepository.findAll(pageable);

		return new PageResponse<>(page.getContent().stream().map(this::toResponseDTO).toList(), page.getNumber(),
				page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
	}

	@Transactional
	public Shop updateShopStatus(String shopId, ShopStatus status) {

		Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

		shop.setShopStatus(status);
		ShopStatusChangedEvent event =
		        ShopStatusChangedEvent.builder()
		                .shopId(shop.getShopId())
		                .userId(shop.getUserId())
		                .shopName(shop.getShopName())
		               .shopStatus(shop.getShopStatus())
		                .build();

		shopKafkaProducer.publish(
		        KafkaTopics.SHOP_EVENTS,
		        event
		);

		return shopRepository.save(shop);

	}

	public FollowShopResponse followShop(String shopId, String userId) {

		if (followerRepository.findByShopIdAndUserId(shopId, userId).isPresent()) {

			return getFollowStatus(shopId, userId);
		}

		ShopFollower follower = new ShopFollower();
		follower.setShopId(shopId);
		follower.setUserId(userId);

		followerRepository.save(follower);

		return getFollowStatus(shopId, userId);
	}

	public FollowShopResponse unfollowShop(String shopId, String userId) {

		followerRepository.deleteByShopIdAndUserId(shopId, userId);

		return getFollowStatus(shopId, userId);
	}

	public FollowShopResponse getFollowStatus(String shopId, String userId) {

		boolean following = followerRepository.findByShopIdAndUserId(shopId, userId).isPresent();

		long followers = followerRepository.countByShopId(shopId);

		return FollowShopResponse.builder().shopId(shopId).following(following).followersCount(followers).build();
	}
}