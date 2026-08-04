# 🔗 Flutter & Spring Boot Integration - Technical Specification

## 📋 Complete Technical Contract

This document defines the exact specifications for Flutter frontend and Spring Boot backend integration to ensure seamless communication.

---

## 🏗️ Part 1: Network & Infrastructure

### Port Configuration (FIXED - DO NOT CHANGE)

```
Frontend (Flutter Web):  http://localhost:56789
Auth Service (Backend):  http://localhost:8080
Shop Service (Backend):  http://localhost:8084
User Service (Backend):  http://localhost:8086
```

### Starting the Applications

**Backend (Auth Service)**:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

**Frontend (Flutter Web)**:
```bash
flutter run -d chrome --web-port=56789
```

---

## 🔐 Part 2: CORS Configuration (CRITICAL)

### Backend: SecurityConfig.java Requirements

Your `SecurityConfig.java` MUST include CORS configuration for both localhost variants:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // MUST allow both localhost variants
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:56789",
            "http://127.0.0.1:56789"
        ));
        
        // MUST allow all these methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // MUST allow these headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
        
        http.cors(Customizer.withDefaults())  // Enable CORS
            .csrf(csrf -> csrf.disable())      // Disable CSRF
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/verify-otp").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

### Testing CORS Configuration

**Preflight Request (Browser will send automatically)**:
```bash
curl -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:56789" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

**Expected Response Headers**:
```
Access-Control-Allow-Origin: http://localhost:56789
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, Accept
Access-Control-Allow-Credentials: true
```

---

## 🔑 Part 3: Authentication API Contract (Port 8080)

### Base URL for All Auth Endpoints
```
http://localhost:8080/api/auth
```

### Endpoint 1: REGISTER

**Specification**:
```
Method:   POST
URL:      /api/auth/register
Status:   201 Created (or 409 Conflict if user exists)
Content:  application/json
```

**Request Body**:
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "phone": "9876543210"
}
```

**Field Validation (Backend)**:
| Field | Rules | Error Message |
|-------|-------|---------------|
| fullName | Required, 3-50 chars | "Full name is required" / "Size must be between 3 and 50" |
| email | Required, unique, valid format | "Email is required" / "Invalid email format" / "User already exists" |
| password | Required, 8-100 chars | "Password is required" / "Size must be between 8 and 100" |
| phone | Required, 10 digits only | "Phone number is required" / "Invalid phone number" |

**Success Response (201)**:
```json
{
  "success": true,
  "message": "OTP sent to phone/email",
  "data": null
}
```

**Error Response (409 - User Exists)**:
```json
{
  "success": false,
  "message": "User already exists with this email",
  "data": null
}
```

**Error Response (400 - Validation Error)**:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "fullName": "Size must be between 3 and 50",
    "email": "Invalid email format",
    "password": "Size must be between 8 and 100",
    "phone": "Invalid phone number"
  }
}
```

**Backend Logic**:
- ✅ Generate 6-digit OTP (use `OtpGenerator.generateOTP()`)
- ✅ Save user with `verified = false`
- ✅ Store OTP with 5-minute expiry
- ✅ Print OTP to console (TODO: Implement SMS/Email)
- ✅ Return 201 status code

---

### Endpoint 2: VERIFY OTP

**Specification**:
```
Method:   POST
URL:      /api/auth/verify-otp
Status:   200 OK (or 400/404 on error)
Content:  application/json
```

**Request Body**:
```json
{
  "email": "john@example.com",
  "otp": "123456"
}
```

**Field Validation**:
| Field | Rules | Error Message |
|-------|-------|---------------|
| email | Required, valid format | "Email is required" |
| otp | Required, exactly 6 digits | "Size must be between 6 and 6" |

**Success Response (200)**:
```json
{
  "success": true,
  "message": "Verified successfully",
  "data": null
}
```

**Error Response (404 - User Not Found)**:
```json
{
  "success": false,
  "message": "User not found",
  "data": null
}
```

**Error Response (400 - Invalid OTP)**:
```json
{
  "success": false,
  "message": "Invalid OTP provided",
  "data": null
}
```

**Error Response (400 - OTP Expired)**:
```json
{
  "success": false,
  "message": "OTP has expired",
  "data": null
}
```

**Backend Logic**:
- ✅ Find user by email
- ✅ Check if OTP matches
- ✅ Check if OTP has expired (current time > otpExpiry)
- ✅ Mark user as `verified = true`
- ✅ Clear OTP fields
- ✅ Return 200 status code

---

### Endpoint 3: LOGIN

**Specification**:
```
Method:   POST
URL:      /api/auth/login
Status:   200 OK (or 401/403 on error)
Content:  application/json
```

**Request Body**:
```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Field Validation**:
| Field | Rules | Error Message |
|-------|-------|---------------|
| email | Required, valid format | "Email is required" / "Invalid email" |
| password | Required, 8-100 chars | "Password is required" / "Size must be between 8 and 100" |

**Success Response (200)**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "john@example.com"
  }
}
```

**Error Response (401 - Bad Credentials)**:
```json
{
  "success": false,
  "message": "Invalid credentials",
  "data": null
}
```

**Error Response (403 - Not Verified)**:
```json
{
  "success": false,
  "message": "Please verify your account first",
  "data": null
}
```

**Error Response (400 - Validation Error)**:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Invalid email",
    "password": "Size must be between 8 and 100"
  }
}
```

**Backend Logic**:
- ✅ Check if user exists
- ✅ Check if user is verified (`verified == true`)
- ✅ Authenticate credentials via `AuthenticationManager`
- ✅ Generate JWT token (5 hours validity)
- ✅ Return token and username
- ✅ Return 200 status code

**JWT Token Details**:
- Algorithm: HS512
- Validity: 5 hours (18000000 ms)
- Subject: user email
- Secret: from `jwt.secret` property

---

### Endpoint 4: HOME (Protected)

**Specification**:
```
Method:   GET
URL:      /api/auth/home
Status:   200 OK (or 401 on error)
Content:  application/json
Auth:     REQUIRED - Authorization: Bearer {token}
```

**Request Headers** (MANDATORY):
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Success Response (200)**:
```json
{
  "success": true,
  "message": "Welcome",
  "data": {
    "email": "john@example.com",
    "message": "Welcome to Home",
    "features": ["Reels", "Shop", "Chat"]
  }
}
```

**Error Response (401 - No Token)**:
```json
{
  "success": false,
  "message": "Access Denied !! Full authentication is required to access this resource",
  "data": null
}
```

**Error Response (401 - Invalid Token)**:
```json
{
  "success": false,
  "message": "Access Denied !! Invalid or expired token",
  "data": null
}
```

**Error Response (401 - Token Expired)**:
```json
{
  "success": false,
  "message": "Access Denied !! Token is expired",
  "data": null
}
```

---

## 🔐 Part 4: Token Security

### Storage Requirements

**Frontend (Flutter)**:
```dart
// Option 1: Secure Storage (Recommended)
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

const storage = FlutterSecureStorage();

// Store token after login
await storage.write(
  key: 'jwtToken',
  value: token,
);

// Retrieve token for API calls
final token = await storage.read(key: 'jwtToken');

// Delete token on logout
await storage.delete(key: 'jwtToken');
```

```dart
// Option 2: SharedPreferences (Less Secure)
import 'package:shared_preferences/shared_preferences.dart';

final prefs = await SharedPreferences.getInstance();

// Store
await prefs.setString('jwtToken', token);

// Retrieve
final token = prefs.getString('jwtToken');

// Delete
await prefs.remove('jwtToken');
```

### Token Attachment Rules

**RULE 1: Auth Endpoints (NO TOKEN)**
These endpoints must NOT include Authorization header:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/verify-otp`

Reason: User not yet authenticated

**RULE 2: Protected Endpoints (REQUIRES TOKEN)**
All requests to shop, user, or profile endpoints MUST include token:
```
GET    /api/shop/nearby
POST   /api/shop/add-to-cart
GET    /api/user/profile
PUT    /api/user/settings
```

Header format:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Refresh Strategy

**Current Implementation**: 5-hour validity
```java
@Value("${jwt.expiration}")
private long jwtExpiration;  // = 18000000 ms (5 hours)
```

**Frontend Logic**:
```dart
// Check if token exists and is still valid
Future<bool> isTokenValid() async {
  final token = await storage.read(key: 'jwtToken');
  if (token == null) return false;
  
  // Decode JWT (without verification)
  final parts = token.split('.');
  if (parts.length != 3) return false;
  
  final payload = jsonDecode(
    utf8.decode(base64Url.decode(parts[1]))
  );
  
  final expiry = DateTime.fromMillisecondsSinceEpoch(payload['exp'] * 1000);
  return DateTime.now().isBefore(expiry);
}

// Auto-logout on token expiry
void checkTokenExpiry() async {
  bool valid = await isTokenValid();
  if (!valid) {
    await logout();
    navigateToLogin();
  }
}
```

---

## 🔄 Part 5: API Request/Response Flow

### Standard Request Flow

```
Frontend (Flutter)
      ↓
1. Prepare request body
   - Validate locally first
   - Serialize to JSON
      ↓
2. Add headers
   - Content-Type: application/json
   - Authorization: Bearer {token} (if protected endpoint)
      ↓
3. Make HTTP request
   - Use http.post() or http.get()
   - URL: http://localhost:8080/api/auth/...
      ↓
Backend (Spring Boot)
      ↓
4. Receive request
   - Parse JSON body
   - Extract headers
      ↓
5. Security check
   - If protected: Validate JWT token
   - If public: Skip token validation
      ↓
6. Validate input
   - Check field constraints
   - Return 400 if invalid
      ↓
7. Process business logic
   - Database operations
   - Generate OTP/Token
      ↓
8. Return response
   - Standard format: { success, message, data }
   - Appropriate status code
      ↓
Frontend (Flutter)
      ↓
9. Parse response
   - Deserialize JSON
   - Check response.success
      ↓
10. Handle result
    - If success: Show data to user
    - If error: Show error message to user
    - Store token if login response
```

---

## 📊 Error Handling Matrix

### HTTP Status Codes Used

| Status | Scenario | Response Format |
|--------|----------|-----------------|
| **200** | Successful request | `{ success: true, message: "...", data: {...} }` |
| **201** | Resource created (register) | `{ success: true, message: "...", data: null }` |
| **400** | Validation error | `{ success: false, message: "Validation failed", data: {...} }` |
| **401** | Missing/invalid token | `{ success: false, message: "Access Denied", data: null }` |
| **403** | User not verified | `{ success: false, message: "Please verify first", data: null }` |
| **404** | User not found | `{ success: false, message: "User not found", data: null }` |
| **409** | User already exists | `{ success: false, message: "User already exists", data: null }` |
| **500** | Server error | `{ success: false, message: "Server error", data: null }` |

### Flutter Error Handling Template

```dart
Future<void> handleAuthRequest(Future<http.Response> request) async {
  try {
    final response = await request;
    final data = jsonDecode(response.body);
    
    switch (response.statusCode) {
      case 200:
      case 201:
        // Success
        if (data['success']) {
          showSuccessMessage(data['message']);
          return data['data'];
        }
        break;
        
      case 400:
        // Validation error
        if (data['data'] is Map) {
          // Field-specific errors
          data['data'].forEach((field, error) {
            showFieldError(field, error);
          });
        } else {
          // General error
          showErrorMessage(data['message']);
        }
        break;
        
      case 401:
        // Token invalid - logout
        await logout();
        navigateToLogin();
        break;
        
      case 403:
        // Account not verified
        showErrorMessage("Please verify your account first");
        break;
        
      case 404:
        // Not found
        showErrorMessage(data['message']);
        break;
        
      case 409:
        // Conflict (user exists)
        showErrorMessage(data['message']);
        break;
        
      case 500:
        // Server error
        showErrorMessage("Server error. Please try again later.");
        break;
    }
  } catch (e) {
    showErrorMessage("Network error: ${e.toString()}");
  }
}
```

---

## ✅ Implementation Checklist

### Backend Requirements
- [ ] CORS configured for both localhost variants
- [ ] All auth endpoints return standard response format
- [ ] Validation errors return 400 with field errors
- [ ] JWT token valid for 5 hours
- [ ] Token includes user email as subject
- [ ] Protected endpoints check for valid token
- [ ] OTP valid for 5 minutes
- [ ] User marked as verified after OTP verification
- [ ] Passwords hashed with BCrypt

### Frontend (Flutter) Requirements
- [ ] Use FlutterSecureStorage for token storage
- [ ] Include Authorization header for protected endpoints
- [ ] Skip Authorization header for auth endpoints
- [ ] Handle all error status codes (400, 401, 403, 404, 409)
- [ ] Validate input before sending request
- [ ] Show field-specific validation errors
- [ ] Implement auto-logout on 401
- [ ] Store token after successful login
- [ ] Clear token on logout
- [ ] Show loading indicators during API calls

---

## 🧪 Testing Endpoints

### Test 1: Register (Should Succeed)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:56789" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "TestPass123",
    "phone": "9876543210"
  }'
```

Expected: 201 + OTP printed to console

### Test 2: Verify OTP (Should Succeed with correct OTP)
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:56789" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

Expected: 200 if OTP matches

### Test 3: Login (Should Succeed after verification)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:56789" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123"
  }'
```

Expected: 200 + JWT token in response

### Test 4: Protected Endpoint (Should Fail without token)
```bash
curl -X GET http://localhost:8080/api/auth/home \
  -H "Origin: http://localhost:56789"
```

Expected: 401 Unauthorized

### Test 5: Protected Endpoint (Should Succeed with token)
```bash
curl -X GET http://localhost:8080/api/auth/home \
  -H "Authorization: Bearer {token}" \
  -H "Origin: http://localhost:56789"
```

Expected: 200 + user data

---

## 📝 Summary Table

| Aspect | Requirement | Value |
|--------|-------------|-------|
| **Frontend Port** | Fixed | 56789 |
| **Backend Auth Port** | Fixed | 8080 |
| **Backend API Prefix** | All auth endpoints | /api/auth |
| **CORS Origins** | Must allow | localhost:56789, 127.0.0.1:56789 |
| **Token Validity** | JWT expiry | 5 hours |
| **OTP Validity** | OTP expiry | 5 minutes |
| **Token Storage** | Frontend | FlutterSecureStorage |
| **Auth Endpoints** | No token required | register, login, verify-otp |
| **Protected Endpoints** | Token required | home (+ others) |
| **Session Type** | Stateless | JWT-based |
| **CSRF Protection** | Disabled | (JWT doesn't need CSRF) |

---

Generated: May 25, 2026
Version: 1.0
For: Flutter + Spring Boot Integration
