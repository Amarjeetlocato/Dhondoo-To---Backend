/*
 * package com.whoami.launch.controller;
 * 
 * import org.springframework.http.HttpStatus; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.RequestBody; import
 * org.springframework.web.bind.annotation.RequestHeader; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * import com.whoami.launch.dto.ApiResponse; import
 * com.whoami.launch.security.GatewayTokenValidator;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 *//**
	 * Token Validation Controller Internal API for validating tokens from gateway
	 * and other services
	 */
/*
 * @RestController
 * 
 * @RequestMapping("/internal/token")
 * 
 * @Slf4j public class TokenValidationController {
 * 
 * private final GatewayTokenValidator tokenValidator;
 * 
 * public TokenValidationController( GatewayTokenValidator tokenValidator) {
 * this.tokenValidator = tokenValidator; }
 * 
 *//**
	 * Validate token from Authorization header GET /internal/token/validate
	 */
/*
 * @GetMapping("/validate") public
 * ResponseEntity<ApiResponse<GatewayTokenValidator.TokenValidationResult>>
 * validateTokenFromHeader(
 * 
 * @RequestHeader(value = "Authorization", required = false) String
 * authorizationHeader) {
 * 
 * if (authorizationHeader == null || authorizationHeader.isEmpty()) { return
 * ResponseEntity .status(HttpStatus.UNAUTHORIZED) .body(new ApiResponse<>(
 * false, "Authorization header is missing", null)); }
 * 
 * String token = tokenValidator .extractTokenFromHeader(authorizationHeader);
 * 
 * if (token == null) { return ResponseEntity .status(HttpStatus.UNAUTHORIZED)
 * .body(new ApiResponse<>( false, "Invalid Authorization header format",
 * null)); }
 * 
 * GatewayTokenValidator.TokenValidationResult result =
 * tokenValidator.validateToken(token);
 * 
 * if (!result.isValid()) { return ResponseEntity
 * .status(HttpStatus.UNAUTHORIZED) .body(new ApiResponse<>( false,
 * result.getMessage(), null)); }
 * 
 * log.info("Token validated for user: {}", result.getEmail());
 * 
 * return ResponseEntity.ok( new ApiResponse<>( true, "Token is valid",
 * result)); }
 * 
 *//**
	 * Validate token from request body POST /internal/token/validate-token
	 */
/*
 * @PostMapping("/validate-token") public
 * ResponseEntity<ApiResponse<GatewayTokenValidator.TokenValidationResult>>
 * validateTokenFromBody(
 * 
 * @RequestBody TokenValidationRequest request) {
 * 
 * if (request.getToken() == null || request.getToken().isEmpty()) { return
 * ResponseEntity .status(HttpStatus.BAD_REQUEST) .body(new ApiResponse<>(
 * false, "Token is required", null)); }
 * 
 * GatewayTokenValidator.TokenValidationResult result =
 * tokenValidator.validateToken(request.getToken());
 * 
 * if (!result.isValid()) { return ResponseEntity
 * .status(HttpStatus.UNAUTHORIZED) .body(new ApiResponse<>( false,
 * result.getMessage(), null)); }
 * 
 * log.info("Token validated for user: {}", result.getEmail());
 * 
 * return ResponseEntity.ok( new ApiResponse<>( true, "Token is valid",
 * result)); }
 * 
 *//**
	 * Get token details GET /internal/token/details
	 */
/*
 * @GetMapping("/details") public ResponseEntity<ApiResponse<TokenDetails>>
 * getTokenDetails(
 * 
 * @RequestHeader(value = "Authorization", required = false) String
 * authorizationHeader) {
 * 
 * if (authorizationHeader == null || authorizationHeader.isEmpty()) { return
 * ResponseEntity .status(HttpStatus.UNAUTHORIZED) .body(new ApiResponse<>(
 * false, "Authorization header is missing", null)); }
 * 
 * String token = tokenValidator .extractTokenFromHeader(authorizationHeader);
 * 
 * if (token == null) { return ResponseEntity .status(HttpStatus.UNAUTHORIZED)
 * .body(new ApiResponse<>( false, "Invalid Authorization header format",
 * null)); }
 * 
 * GatewayTokenValidator.TokenValidationResult result =
 * tokenValidator.validateToken(token);
 * 
 * if (!result.isValid()) { return ResponseEntity
 * .status(HttpStatus.UNAUTHORIZED) .body(new ApiResponse<>( false,
 * result.getMessage(), null)); }
 * 
 * TokenDetails details = TokenDetails.builder() .email(result.getEmail())
 * .adminId(result.getAdminId()) .isValid(result.isValid()) .build();
 * 
 * log.info("Token details retrieved for user: {}", result.getEmail());
 * 
 * return ResponseEntity.ok( new ApiResponse<>( true, "Token details retrieved",
 * details)); }
 * 
 *//**
	 * Quick token validation (just yes/no) GET /internal/token/is-valid
	 */
/*
 * @GetMapping("/is-valid") public ResponseEntity<ApiResponse<Boolean>>
 * isTokenValid(
 * 
 * @RequestHeader(value = "Authorization", required = false) String
 * authorizationHeader) {
 * 
 * if (authorizationHeader == null || authorizationHeader.isEmpty()) { return
 * ResponseEntity.ok( new ApiResponse<>(true, "Invalid token", false)); }
 * 
 * String token = tokenValidator .extractTokenFromHeader(authorizationHeader);
 * 
 * if (token == null) { return ResponseEntity.ok( new ApiResponse<>(true,
 * "Invalid token format", false)); }
 * 
 * boolean isValid = tokenValidator.isTokenValid(token);
 * 
 * return ResponseEntity.ok( new ApiResponse<>( true, isValid ? "Valid" :
 * "Invalid", isValid)); }
 * 
 *//**
	 * Request DTO for token validation
	 */
/*
 * public static class TokenValidationRequest {
 * 
 * private String token;
 * 
 * public TokenValidationRequest() {}
 * 
 * public TokenValidationRequest(String token) { this.token = token; }
 * 
 * public String getToken() { return token; }
 * 
 * public void setToken(String token) { this.token = token; } }
 * 
 *//**
	 * Token Details DTO
	 *//*
		 * public static class TokenDetails {
		 * 
		 * private String email; private String adminId; private boolean isValid;
		 * 
		 * public TokenDetails() {}
		 * 
		 * public TokenDetails(String email, String adminId, boolean isValid) {
		 * this.email = email; this.adminId = adminId; this.isValid = isValid; }
		 * 
		 * public static Builder builder() { return new Builder(); }
		 * 
		 * public String getEmail() { return email; }
		 * 
		 * public String getAdminId() { return adminId; }
		 * 
		 * public boolean isValid() { return isValid; }
		 * 
		 * public static class Builder {
		 * 
		 * private String email; private String adminId; private boolean isValid;
		 * 
		 * public Builder email(String email) { this.email = email; return this; }
		 * 
		 * public Builder adminId(String adminId) { this.adminId = adminId; return this;
		 * }
		 * 
		 * public Builder isValid(boolean isValid) { this.isValid = isValid; return
		 * this; }
		 * 
		 * public TokenDetails build() { return new TokenDetails(email, adminId,
		 * isValid); } } } }
		 */