# Security Monitoring Implementation Guide

## Overview
Ye production-level security implementation aapke SuperUser service ke saath integrate ho gaya hai. Ye system track karega:
- Active user sessions
- Blocked IP addresses
- Security threats (audit logs)
- System health

---

## Database Migrations (SQL)

Run these queries to create required tables:

```sql
-- User Sessions Table
CREATE TABLE user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    admin_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    token LONGTEXT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(500),
    login_time DATETIME NOT NULL,
    last_access_time DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    logout_time DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (admin_id) REFERENCES admin(id)
);

-- Blocked IPs Table
CREATE TABLE blocked_ips (
    id VARCHAR(255) PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL UNIQUE,
    blocked_at DATETIME NOT NULL,
    expires_at DATETIME,
    reason VARCHAR(255) NOT NULL,
    failed_attempts INT,
    is_permanent BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- Update Admin Audit Logs Table (if needed)
ALTER TABLE admin_audit_logs 
ADD COLUMN severity VARCHAR(20) DEFAULT 'MEDIUM',
ADD COLUMN description TEXT;
```

---

## Integration Steps

### 1. Update pom.xml

```xml
<!-- Add these dependencies if not already present -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Already should be present -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 2. Update application.properties

```properties
# ====== ACTUATOR CONFIGURATION ======
management.endpoints.web.exposure.include=health,metrics
management.endpoint.health.show-details=always
management.endpoint.health.probes.enabled=true

# ====== SECURITY DATABASE ======
# Already configured in your datasource
```

### 3. Update AdminAuthController (Login Flow)

```java
@Autowired
private SecurityService securityService;

@Autowired
private SecurityUtils securityUtils;

@PostMapping("/login")
public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody AdminLoginRequest request) {
    try {
        String ipAddress = SecurityUtils.getClientIpAddress();
        
        // Check if IP is blocked
        if (securityService.isIpBlocked(ipAddress)) {
            securityUtils.logSecurityEvent(
                    null, 
                    "BLOCKED_IP_LOGIN", 
                    null, 
                    ipAddress, 
                    Severity.CRITICAL, 
                    "Login attempt from blocked IP"
            );
            throw new AccessDeniedException("IP blocked due to security policy");
        }

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    securityUtils.trackFailedLogin(ipAddress, null);
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            securityUtils.trackFailedLogin(ipAddress, admin.getAdminId());
            throw new RuntimeException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(admin.getAdminId());
        String refreshToken = jwtUtil.generateRefreshToken(admin.getAdminId());

        // Track successful login
        securityUtils.trackSuccessfulLogin(
                admin.getAdminId(),
                accessToken,
                ipAddress,
                SecurityUtils.getUserAgent()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Login successful",
                new JwtResponse(accessToken, refreshToken)
        ));

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, e.getMessage(), null));
    }
}

@PostMapping("/logout")
public ResponseEntity<ApiResponse<String>> logout() {
    try {
        String ipAddress = SecurityUtils.getClientIpAddress();
        String adminId = getLoggedInAdminId(); // Extract from JWT

        securityUtils.trackLogout(adminId, ipAddress);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Logout successful",
                null
        ));

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, e.getMessage(), null));
    }
}
```

### 4. Create JWT Filter with Security

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String ipAddress = SecurityUtils.getClientIpAddress();

            // Check if IP is blocked
            if (securityService.isIpBlocked(ipAddress)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("IP blocked");
                return;
            }

            String token = extractToken(request);

            if (token != null && jwtUtil.validateToken(token)) {
                String adminId = jwtUtil.extractUsername(token);
                // Continue with request
            } else if (token != null) {
                securityUtils.logInvalidTokenAttempt(ipAddress, token.substring(0, 20));
            }

        } catch (Exception e) {
            System.out.println("JWT Filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

---

## API Endpoints

### Security Statistics
```
GET /api/admin/security/stats

Response:
{
  "success": true,
  "message": "Security statistics fetched successfully",
  "data": {
    "activeSessions": 5,
    "blockedIps": 2,
    "threatsDetected": 8,
    "systemHealth": "EXCELLENT",
    "totalSessions": 12,
    "criticalThreats": 2,
    "highThreats": 3,
    "mediumThreats": 2,
    "lowThreats": 1
  }
}
```

### Active Sessions
```
GET /api/admin/security/active-sessions
```

### Blocked IPs
```
GET /api/admin/security/blocked-ips
```

### Threats
```
GET /api/admin/security/threats?hours=24
```

### Check IP Status
```
GET /api/admin/security/check-ip?ip=192.168.1.1
```

### Block IP
```
POST /api/admin/security/block-ip?ip=192.168.1.1&reason=Brute%20Force&hours=24
```

### Unblock IP
```
POST /api/admin/security/unblock-ip?ip=192.168.1.1
```

---

## Key Features

### 1. Automatic IP Blocking
- After 20 failed login attempts, IP is automatically blocked
- Blocks expire after 24 hours (configurable)

### 2. Session Tracking
- Every login creates a session record
- Tracks IP, User Agent, login time
- Sessions can be manually invalidated

### 3. Threat Detection
- Critical events are logged with severity levels
- Count threats in last 24 hours via API
- Automatic CRITICAL alerts for:
  - Invalid JWT tokens
  - SQL injection attempts
  - Multiple TOTP failures

### 4. System Health
- Spring Boot Actuator integration
- Automatically checks database, cache health
- Returns EXCELLENT, DEGRADED, or DOWN status

---

## Security Best Practices

1. **Run cleanup regularly** - Delete old sessions/expired blocks
   ```
   POST /api/admin/security/cleanup
   ```

2. **Monitor threats dashboard** - Check security stats daily

3. **IP Whitelisting** - Add trusted IPs to prevent blocking

4. **Rotating Secrets** - Change JWT secret periodically

5. **Rate Limiting** - Implement at API Gateway level

---

## Logging Examples

```java
// Failed login
securityUtils.trackFailedLogin(ipAddress, adminId);

// Successful login
securityUtils.trackSuccessfulLogin(adminId, token, ipAddress, userAgent);

// Logout
securityUtils.trackLogout(adminId, ipAddress);

// Invalid token
securityUtils.logInvalidTokenAttempt(ipAddress, tokenPart);

// SQL Injection attempt
securityUtils.logSqlInjectionAttempt(ipAddress, "user_input");

// TOTP failure
securityUtils.logTotpFailure(adminId, ipAddress, failureCount);

// Custom suspicious activity
securityUtils.logSuspiciousActivity(
    adminId, 
    "Unusual access pattern", 
    ipAddress, 
    Severity.HIGH
);
```

---

## Database Queries

### Get active sessions count
```sql
SELECT COUNT(*) FROM user_sessions WHERE is_active = TRUE;
```

### Get recently blocked IPs
```sql
SELECT * FROM blocked_ips 
WHERE (is_permanent = TRUE OR expires_at > NOW())
ORDER BY blocked_at DESC
LIMIT 10;
```

### Get threats in last 24 hours
```sql
SELECT COUNT(*) FROM admin_audit_logs 
WHERE severity = 'CRITICAL' 
AND created_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR);
```

### Cleanup old sessions
```sql
DELETE FROM user_sessions 
WHERE is_active = FALSE 
AND logout_time <= DATE_SUB(NOW(), INTERVAL 30 DAY);
```

---

## Scheduled Tasks (Optional)

Add to your SecurityService:

```java
@Scheduled(fixedDelay = 3600000) // Run every hour
public void scheduledCleanup() {
    securityService.cleanup();
    System.out.println("[SECURITY] Scheduled cleanup completed");
}
```

Then add to main class:
```java
@SpringBootApplication
@EnableScheduling
public class SuperUSerApplication {
    //...
}
```

---

Ye complete implementation production-ready hai! Start with database migrations, then integrate with your login flow.
