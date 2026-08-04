package com.whoami.launch.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.AdminLoginRequest;
import com.whoami.launch.dto.AdminRegisterRequest;
import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.entity.Admin;
import com.whoami.launch.entity.RefreshToken;
import com.whoami.launch.exception.AdminNotFoundException;
import com.whoami.launch.exception.InvalidTotpException;
import com.whoami.launch.exception.TotpNotConfiguredException;
import com.whoami.launch.repository.AdminRepository;
import com.whoami.launch.repository.RefreshTokenRepository;
import com.whoami.launch.security.JwtHelper;
import com.whoami.launch.service.AdminService;
import com.whoami.launch.service.AuditService;
import com.whoami.launch.service.TotpService;

@Service
public class AdminServiceImpl
        implements AdminService {

    private final AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;
    private final TotpService totpService;
    private final AuditService auditService;

    public AdminServiceImpl(
            AdminRepository adminRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtHelper jwtHelper,
            TotpService totpService,
            AuditService auditService
    ) {
        this.adminRepository = adminRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtHelper = jwtHelper;
        this.totpService = totpService;
        this.auditService = auditService;
    }

    @Override
    public String register(
            AdminRegisterRequest request
    ) {

        if (adminRepository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Admin already exists");
        }

        String secret =
                totpService.generateSecret();

        Admin admin =
                Admin.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()))
                        .totpSecret(secret)
                        .enabled(true)
                        .build();

        adminRepository.save(admin);

        return "Admin created successfully";
    }

    @Override
    public String generateQrCode(
            String email
    ) {

        Admin admin =
                adminRepository.findByEmail(email)
                        .orElseThrow();

        return totpService.generateQrCodeUrl(
                email,
                admin.getTotpSecret()
        );
    }

    @Override
    public String verifyTotpSetup(
            String email,
            String code
    ) {

        try {

            System.out.println(
                    "\n===== VERIFY TOTP START =====");

            System.out.println(
                    "EMAIL : " + email);

            System.out.println(
                    "CODE : " + code);

            Admin admin =
                    adminRepository
                            .findByEmail(email)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Admin not found"));

            System.out.println(
                    "ADMIN FOUND : "
                            + admin.getEmail());

            System.out.println(
                    "SECRET : "
                            + admin.getTotpSecret());

            boolean valid =
                    totpService.verifyCode(
                            admin.getTotpSecret(),
                            code
                    );

            System.out.println(
                    "VALID RESULT : "
                            + valid);

            if (!valid) {

                throw new RuntimeException(
                        "Invalid code");
            }

            admin.setTotpEnabled(true);

            adminRepository.save(admin);

            System.out.println(
                    "TOTP ENABLED SUCCESSFULLY");

            return "TOTP Enabled";

        } catch (Exception e) {

            System.out.println(
                    "\n===== VERIFY TOTP ERROR =====");

            System.out.println(
                    "ERROR TYPE : "
                            + e.getClass().getName());

            System.out.println(
                    "ERROR MSG : "
                            + e.getMessage());

            e.printStackTrace();

            throw e;
        }
    }
    @Override
    public JwtResponse login(
            AdminLoginRequest request
    ) {

        System.out.println("\n========== ADMIN LOGIN START ==========");

        System.out.println("Username/Email : "
                + request.getUsernameOrEmail());

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

        } catch (Exception ex) {

            System.out.println("INVALID USERNAME/PASSWORD");

            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }

        Admin admin =
                adminRepository
                        .findByEmailOrUsername(
                                request.getUsernameOrEmail(),
                                request.getUsernameOrEmail()
                        )
                        .orElseThrow(() ->
                                new AdminNotFoundException(
                                        "Admin account not found"
                                )
                        );

        System.out.println("Admin Found : "
                + admin.getEmail());

        if (!Boolean.TRUE.equals(
                admin.getTotpEnabled()
        )) {

            System.out.println("TOTP NOT CONFIGURED");

            throw new TotpNotConfiguredException(
                    "Google Authenticator is not configured for this account"
            );
        }

        boolean valid =
                totpService.verifyCode(
                        admin.getTotpSecret(),
                        request.getTotpCode()
                );

        System.out.println("TOTP RESULT : " + valid);

        if (!valid) {

            throw new InvalidTotpException(
                    "Invalid authenticator code"
            );
        }

        String accessToken =
                jwtHelper.generateToken(
                        admin.getAdminId(),
                        admin.getEmail()
                );

        String refreshToken =
                jwtHelper.generateRefreshToken(
                        admin.getAdminId(),
                        admin.getEmail()
                );

        RefreshToken token =
                RefreshToken.builder()
                        .token(refreshToken)
                        .admin(admin)
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(7)
                        )
                        .build();

        refreshTokenRepository.save(token);

        auditService.log(
                admin.getAdminId(),
                "LOGIN",
                admin.getAdminId(),
                "SYSTEM"
        );

        System.out.println("LOGIN SUCCESS");

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .adminId(admin.getAdminId())
                .email(admin.getEmail())
                .build();
    }
    @Override
    public JwtResponse refreshToken(
            String refreshToken
    ) {

        RefreshToken token =
                refreshTokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow();

        Admin admin = token.getAdmin();

        String accessToken =
                jwtHelper.generateToken(
                        admin.getAdminId(),
                        admin.getEmail()
                );

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .adminId(admin.getAdminId())
                .email(admin.getEmail())
                .build();
    }
}