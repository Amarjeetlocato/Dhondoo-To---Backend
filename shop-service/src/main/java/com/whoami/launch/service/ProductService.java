package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.dto.ProductCreatedEvent;
import com.locato.dto.ProductDeletedEvent;
import com.locato.dto.ProductUpdatedEvent;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ProductSummaryDTO;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.entity.Product;
import com.whoami.launch.entity.Shop;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.enums.StockStatus;
import com.whoami.launch.producer.ShopKafkaProducer;
import com.whoami.launch.repository.CustomerProfileRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ShopFollowerRepository;
import com.whoami.launch.repository.ShopRepository;
import com.locato.topics.KafkaTopics;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ShopRepository shopRepository;

	@Autowired
	private CustomerProfileRepository customerProfileRepository;
	
	
	@Autowired
	private ShopFollowerRepository followerRepository;

	@Autowired
	private ShopKafkaProducer shopKafkaProducer;

	private com.whoami.launch.util.notify notify;

	public Product createProduct(Product product) {

		Shop shop = shopRepository.findById(product.getShopId())
				.orElseThrow(() -> new RuntimeException("Shop not found with id: " + product.getShopId()));

		if (product.getStockStatus() == null) {
			product.setStockStatus(StockStatus.AVAILABLE);
		}

		if (product.getVisibility() == null) {
			product.setVisibility(ProductVisibility.PUBLIC);
		}

		Product savedProduct = productRepository.save(product);

		CustomerProfile profile =
		        customerProfileRepository
		                .findByUserId(shop.getUserId())
		                .orElse(null);

		String productImage = null;

		if (savedProduct.getProductImages() != null
		        && !savedProduct.getProductImages().isEmpty()) {

		    productImage =
		            savedProduct.getProductImages().get(0);
		}

		String shopLogo =
		        profile != null
		                ? profile.getLogoUrl()
		                : null;

		String shopBanner =
		        profile != null
		                ? profile.getBannerUrl()
		                : null;

		ProductCreatedEvent event =
		        ProductCreatedEvent.builder()
		                .eventId(UUID.randomUUID().toString())
		                .eventType("PRODUCT_CREATED")
		                .eventTime(LocalDateTime.now())

		                .productId(savedProduct.getProductId())
		                .shopId(shop.getShopId())
		                .shopName(shop.getShopName())
		                .userId(shop.getUserId())

		                .productName(savedProduct.getProductName())
		                .productPrice(savedProduct.getProductPrice())

		                .productImages(savedProduct.getProductImages())
		                .shopLogo(shopLogo)
		                .shopBanner(shopBanner)

		                .build();
		shopKafkaProducer.publish(com.locato.topics.KafkaTopics.PRODUCT_EVENTS, event);

		return savedProduct;
	}

	public PageResponse<ProductResponseDTO> getAllproducts(Pageable pageable) {

		Page<Product> page = productRepository.findAll(pageable);

		return new PageResponse<>(page.getContent().stream().map(this::toResponseDTO).toList(), page.getNumber(),
				page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
	}

	public Product updateProduct(String productId, Product productDetails) {

		Optional<Product> productOpt = productRepository.findById(productId);

		if (productOpt.isEmpty()) {
			return null;
		}

		Product existingProduct = productOpt.get();

		Shop shop = shopRepository.findById(existingProduct.getShopId()).orElse(null);

		StringBuilder changes = new StringBuilder();

		if (productDetails.getProductName() != null) {
			existingProduct.setProductName(productDetails.getProductName());
			changes.append("Name updated. ");
		}

		if (productDetails.getProductDescription() != null) {
			existingProduct.setProductDescription(productDetails.getProductDescription());
			changes.append("Description updated. ");
		}

		if (productDetails.getProductPrice() != null) {
			existingProduct.setProductPrice(productDetails.getProductPrice());
			changes.append("Price updated. ");
		}

		if (productDetails.getStockStatus() != null) {
			existingProduct.setStockStatus(productDetails.getStockStatus());
			changes.append("Stock status updated. ");
		}

		if (productDetails.getVisibility() != null) {
			existingProduct.setVisibility(productDetails.getVisibility());
			changes.append("Visibility updated. ");
		}

		if (productDetails.getQuality() != null) {
			existingProduct.setQuality(productDetails.getQuality());
			changes.append("Quality updated. ");
		}

		if (productDetails.getOrderType() != null) {
			existingProduct.setOrderType(productDetails.getOrderType());
			changes.append("Order type updated. ");
		}

		if (productDetails.getBadges() != null) {
			existingProduct.setBadges(productDetails.getBadges());
			changes.append("Badges updated. ");
		}

		if (productDetails.getProductImages() != null) {
			existingProduct.setProductImages(productDetails.getProductImages());
			changes.append("Images updated. ");
		}
		
		
		
		
		Product updatedProduct = productRepository.save(existingProduct);
		
		String productImage = null;

		if (updatedProduct.getProductImages() != null
		        && !updatedProduct.getProductImages().isEmpty()) {

		    productImage =
		            updatedProduct.getProductImages().get(0);
		}
		ProductUpdatedEvent event =
		        ProductUpdatedEvent.builder()
		                .eventId(UUID.randomUUID().toString())
		                .eventType("PRODUCT_UPDATED")
		                .eventTime(LocalDateTime.now())

		                .productId(updatedProduct.getProductId())
		                .shopId(shop.getShopId())
		                .shopName(shop.getShopName())
		                .userId(shop.getUserId())

		                .productName(updatedProduct.getProductName())
		                .productImages(updatedProduct.getProductImages())
		                .productPrice(updatedProduct.getProductPrice())

		                .changes(changes.toString())
		                .build();
		
		

		shopKafkaProducer.publish(
		        com.locato.topics.KafkaTopics.PRODUCT_EVENTS,
		        event
		);

		return updatedProduct;
		
	}

	public Optional<Product> getProductById(String productId) {
		return productRepository.findById(productId);
	}

	public List<Product> getProductsByName(String productName) {
		return productRepository.findByProductName(productName);
	}

	public List<Product> searchProducts(String productName) {
		return productRepository.findByProductNameContaining(productName);
	}

	public List<Product> getProductsByShopId(String shopId) {
		return productRepository.findByShopId(shopId);
	}

	public List<Product> getProductsByVisibility(ProductVisibility visibility) {
		return productRepository.findByVisibility(visibility);
	}

	public List<Product> getProductsByBadges(String badges) {
		return productRepository.findByBadges(badges);
	}

	public List<Product> getProductsByQuality(String quality) {
		return productRepository.findByQuality(quality);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public void deleteProduct(String productId) {

		Optional<Product> productOpt = productRepository.findById(productId);

		if (productOpt.isPresent()) {

			Product product = productOpt.get();
			Shop shop = shopRepository
			        .findById(product.getShopId())
			        .orElseThrow(() ->
			                new RuntimeException(
			                        "Shop not found for product: "
			                                + product.getProductId()));
			productRepository.deleteById(productId);

			ProductDeletedEvent event =
			        ProductDeletedEvent.builder()
			                .eventId(UUID.randomUUID().toString())
			                .eventType("PRODUCT_DELETED")
			                .eventTime(LocalDateTime.now())

			                .productId(product.getProductId())
			                .shopId(shop.getShopId())
			                .shopName(shop.getShopName())
			                .userId(shop.getUserId())

			                .productName(product.getProductName())
			                .build();
			shopKafkaProducer.publish(
				    KafkaTopics.PRODUCT_EVENTS,
				    event
				);
				
		}
	}

	public ProductResponseDTO toResponseDTO(Product product) {

		if (product == null) {
			return null;
		}

		return new ProductResponseDTO(product.getProductId(), product.getShopId(), product.getProductName(),
				product.getProductPrice(), product.getProductDescription(), product.getStockStatus(),
				product.getProductImages(), product.getOrderType(), product.getBadges(), product.getVisibility(),
				product.getQuality());
	}

	public ProductSummaryDTO toSummaryDTO(Product product) {

		if (product == null) {
			return null;
		}

		return new ProductSummaryDTO(product.getProductId(), product.getProductName(), product.getShopId(),
				product.getProductPrice(), product.getProductDescription());
	}

	

	
}