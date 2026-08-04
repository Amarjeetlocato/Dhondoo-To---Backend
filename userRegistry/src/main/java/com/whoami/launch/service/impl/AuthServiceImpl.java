package com.whoami.launch.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.CustomerProfileCreatedEvent;
import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.dto.LocationCreatedEvent;
import com.whoami.launch.dto.LocationCreatedEvent;
import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.RegisterRequest;
import com.whoami.launch.entity.User;
import com.whoami.launch.exception.InvalidOtpException;
import com.whoami.launch.exception.UserAlreadyExistsException;
import com.whoami.launch.exception.UserNotFoundException;
import com.whoami.launch.repository.UserRepository;
import com.whoami.launch.security.JwtHelper;
import com.whoami.launch.service.AuthService;
import com.whoami.launch.service.EmailService;
import com.whoami.launch.service.OtpService;
import com.whoami.launch.util.OtpGenerator;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private OtpService otpService;

	@Autowired
	private OtpGenerator otpGenerator;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

	@Override
	public JwtResponse login(LoginRequest request) {

	    User user = userRepository
	            .findByEmailOrUsername(
	                    request.getUsernameOrEmail(),
	                    request.getUsernameOrEmail()
	            )
	            .orElseThrow(() ->
	                    new BadCredentialsException(
	                            "Invalid credentials, please register."
	                    )
	            );

	    if (!user.isVerified()) {
	        throw new RuntimeException("Please verify your account first");
	    }

	    doAuthenticate(
	            request.getUsernameOrEmail(),
	            request.getPassword()
	    );

	    String accessToken = jwtHelper.generateToken(
	            user.getUserId(),
	            user.getEmail()
	    );

	    String refreshToken = jwtHelper.generateRefreshToken(
	            user.getUserId(),
	            user.getEmail()
	    );

	    try {
	        emailService.sendNewLoginEmail(
	                user.getEmail(),
	                user.getUsername(),
	                "Chrome Windows",
	                LocalDateTime.now().toString()
	        );
	    } catch (Exception e) {
	        logger.error("Failed to send login email", e);
	    }

	    return JwtResponse.builder()
	            .jwtToken(accessToken)
	            .refreshToken(refreshToken)
	            .userId(user.getUserId())
	            .email(user.getEmail())
	            .build();
	}
	@Override
	@Transactional
	public String register(RegisterRequest request) {

		if (userRepository.existsByUsername(request.getUsernme())) {

			throw new UserAlreadyExistsException("Username already exists");
		}
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("User already exists");
		}
		User user = new User();

		user.setUsername(request.getUsernme());
		user.setEmail(request.getEmail());

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		user.setPassword(encodedPassword);

		String otp = otpGenerator.generateOTP();

		// HASH OTP
		String hashedOtp = passwordEncoder.encode(otp);

		user.setOtp(hashedOtp);

		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

		user.setVerified(false);

		userRepository.save(user);

		otpService.generateAndSendOtp(request.getEmail(), otp);
		// TODO: Send OTP via email/SMS service
		logger.info("OTP generated for verification");

		return "OTP sent successfully";
	}

	@Override
	public String verifyOtp(String email, String otp) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Invalid request"));

		System.out.println(otp);
		System.out.println(email);
		if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidOtpException("OTP expired");
		}

		boolean isValidOtp = passwordEncoder.matches(otp, user.getOtp());

		if (!isValidOtp) {
			throw new InvalidOtpException("Invalid OTP");
		}

		user.setVerified(true);
		user.setOtp(null);
		user.setOtpExpiry(null);

		userRepository.save(user);

		emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());

		// Customer Event
		CustomerProfileCreatedEvent CustomerProfileCreatedEvent = new CustomerProfileCreatedEvent();
		CustomerProfileCreatedEvent.setUserId(user.getUserId());
		CustomerProfileCreatedEvent.setUsername(user.getUsername());
		CustomerProfileCreatedEvent.setEmail(user.getEmail());

		LocationCreatedEvent event = new LocationCreatedEvent();
		event.setUserId(user.getUserId());

		kafkaTemplate.send("location-created-topic", event);// Publish Events
		// Publish Customer Event
		kafkaTemplate.send("customer-created-topic", CustomerProfileCreatedEvent);

		return "Account verified successfully";
	}

	private void doAuthenticate(String email, String password) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {
			authenticationManager.authenticate(authentication);
		} catch (BadCredentialsException ex) {
			throw new BadCredentialsException("Invalid Username or Password");
		}
	}

	@Override
	public String forgotPassword(String email) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		String otp = otpGenerator.generateOTP();

		user.setResetOtp(passwordEncoder.encode(otp));

		user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(10));

		user.setResetOtpVerified(false);

		userRepository.save(user);

		otpService.generateAndSendOtp(email, otp);

		return "Password reset OTP sent successfully";
	}

	@Override
	public String verifyResetOtp(String email, String otp) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		if (user.getResetOtpExpiry() == null || LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {

			throw new InvalidOtpException("OTP expired");
		}

		boolean isValid = passwordEncoder.matches(otp, user.getResetOtp());

		if (!isValid) {

			throw new InvalidOtpException("Invalid OTP");
		}

		user.setResetOtpVerified(true);

		userRepository.save(user);
		emailService.sendPasswordChangedEmail(email, user.getUsername());

		return "Reset OTP verified successfully";
	}

	@Override
	public String updatePassword(String email, String newPassword) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		if (!Boolean.TRUE.equals(user.getResetOtpVerified())) {

			throw new InvalidOtpException("Please verify OTP first");
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		user.setResetOtp(null);
		user.setResetOtpExpiry(null);
		user.setResetOtpVerified(false);

		userRepository.save(user);
		emailService.sendPasswordUpdatedEmail(email, user.getUsername());

		return "Password updated successfully";
	}

	@Override
	public ApiResponse<Void> deleteUser(String email) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		user.setDeleted(true);
		user.setDeletedAt(LocalDateTime.now());

		userRepository.save(user);

		emailService.sendAccountDeletedEmail(user.getEmail(), user.getUsername());

		return new ApiResponse<>(true, "Account deleted successfully", null);
	}

	@Override
	public ApiResponse<Void> changeEmail(String currentEmail, String newEmail) {

		User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));

		if (userRepository.existsByEmail(newEmail)) {
			throw new RuntimeException("Email already exists");
		}

		user.setEmail(newEmail);

		userRepository.save(user);

		emailService.sendEmailUpdatedEmail(newEmail, user.getUsername());

		return new ApiResponse<>(true, "Email updated successfully", null);
	}

	@Override
	public JwtResponse refreshToken(String refreshToken) {

		if (jwtHelper.isTokenExpired(refreshToken)) {

			throw new RuntimeException("Refresh token expired");
		}

		String email = jwtHelper.getEmailFromToken(refreshToken);

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		String newAccessToken = jwtHelper.generateToken(user.getUserId(), user.getEmail());

		return JwtResponse.builder().jwtToken(newAccessToken).refreshToken(refreshToken).userId(user.getUserId())
				.email(user.getEmail()).build();
	}
}
