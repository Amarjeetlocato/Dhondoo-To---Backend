# Android Emulator Connectivity - Complete Setup Guide

## ✅ Configuration Completed

Your Spring Boot Gateway is now configured for Android emulator connectivity with:

### 1. **Network Configuration** ✓
- `server.address=0.0.0.0` - Listens on all network interfaces
- `server.port=8079` - Gateway port
- `server.netty.connection-timeout=30000ms` - 30 second connection timeout
- `server.netty.idle-timeout=60000ms` - 60 second idle timeout

### 2. **Enhanced Timeout Settings** ✓
- `spring.cloud.gateway.httpclient.connect-timeout=10000ms` - 10 second connect timeout
- `spring.cloud.gateway.httpclient.response-timeout=60s` - 60 second response timeout
- `spring.cloud.gateway.httpclient.pool.pending-acquire-timeout=45000ms` - Connection pool timeouts

### 3. **CORS Configuration** ✓
- Configured in `CorsConfig.java` with emulator origin `10.0.2.2:8079`
- All HTTP methods allowed: GET, POST, PUT, DELETE, PATCH, OPTIONS
- All headers allowed with credentials

### 4. **Service Discovery** ✓
- Eureka enabled for service registration and discovery
- Routes configured for all microservices:
  - `USERREGISTRY` → `/api/auth/**`, `/api/user/**`
  - `SHOP-SERVICE` → `/api/services/**`, `/api/locations/**`, etc.
  - `SERVICE-CHAT` → `/api/chat/**`
  - `OrderService` → `/api/cart/**`, `/api/orders/**`
  - `SuperUser` → `/api/admin/**`

### 5. **Firewall Configuration** ✓
Run this PowerShell command as Administrator:
```powershell
cd c:\Users\ck901\Desktop\locato\Gateway
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\setup-firewall.ps1
```

This will add firewall rules for ports: 8079, 8080, 8084, 8085, 8086, 8761

---

## 🚀 Startup Sequence

### Step 1: Configure Firewall (One-time setup)
```powershell
# Run as Administrator
.\setup-firewall.ps1
```

### Step 2: Start Eureka Service (in separate terminal)
```bash
cd C:\path\to\eureka-server
mvn spring-boot:run
# Verify at http://localhost:8761
```

### Step 3: Start All Microservices (in separate terminals)
```bash
# Terminal for USERREGISTRY (port 8080)
cd C:\path\to\userregistry
mvn spring-boot:run

# Terminal for SHOP-SERVICE (port 8084)
cd C:\path\to\shop-service
mvn spring-boot:run

# Terminal for SERVICE-CHAT (port 8085)
cd C:\path\to\service-chat
mvn spring-boot:run

# Terminal for OrderService (port 8086)
cd C:\path\to\order-service
mvn spring-boot:run

# Terminal for SuperUser (port 8087)
cd C:\path\to\super-user
mvn spring-boot:run
```

### Step 4: Start Gateway (main terminal)
```bash
cd c:\Users\ck901\Desktop\locato\Gateway
mvn spring-boot:run
```

Or use the provided startup script:
```bash
.\start-gateway.cmd
```

---

## 🧪 Verification Tests

### Test 1: Local Gateway Access
```bash
curl http://localhost:8079/
```

### Test 2: Emulator Access
From your Windows host, open browser or curl:
```bash
curl http://10.0.2.2:8079/
```

### Test 3: Eureka Service Discovery
```bash
curl http://localhost:8761/eureka/apps
```

Should show all registered services.

### Test 4: Flutter App Connection
In the Android emulator, the app should successfully connect to:
```
http://10.0.2.2:8079/api/auth/login
```

---

## 🔍 Troubleshooting

### Problem: Connection Timeout (5 seconds)

**Solution 1: Verify Gateway is Running**
```bash
netstat -ano | findstr :8079
```
Should show `0.0.0.0:8079` with state `LISTENING`

**Solution 2: Verify Eureka is Running**
```bash
curl http://localhost:8761
```

**Solution 3: Check Microservices Registration**
```bash
curl http://localhost:8761/eureka/apps
```

### Problem: Firewall Blocking (Site can't be reached)

**Solution:**
```powershell
# Check if rule exists
Get-NetFirewallRule -DisplayName "Android Emulator - Microservices"

# If not exists, run setup script as Administrator
.\setup-firewall.ps1

# IMPORTANT: You may need to restart Windows after adding firewall rules
```

### Problem: Eureka Not Finding Services

**Solution 1: Verify Eureka URL in application.properties**
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**Solution 2: Verify Service Names in application.properties**
Ensure microservices register with exact names:
- `USERREGISTRY`
- `SHOP-SERVICE`
- `SERVICE-CHAT`
- `OrderService`
- `SuperUser`

**Solution 3: Check Service Application Names**
Each microservice's `application.properties` should have:
```properties
spring.application.name=userregistry  # exact match with route URI
```

### Problem: CORS Error in Flutter App

The CORS error usually shows: "No 'Access-Control-Allow-Origin' header"

**Solution: Verify CorsConfig is registered**
```java
@Configuration
public class CorsConfig {
    @Bean
    @Order(-1)
    public CorsWebFilter corsWebFilter() {
        // Already configured in CorsConfig.java
    }
}
```

---

## 📊 Expected Log Output (Gateway Startup)

```
...
2026-06-22 10:30:45 - Started GatewayApplication in 8.234 seconds
2026-06-22 10:30:45 - Registering Eureka service...
2026-06-22 10:30:46 - DiscoveryClient initialized
2026-06-22 10:30:48 - Setting initial instance status to: UP
...
[INFO] Gateway listening on 0.0.0.0:8079
```

---

## 🌐 Alternative: ADB Reverse (Optional)

If 10.0.2.2 doesn't work, you can use port forwarding:

```bash
# In terminal/PowerShell
adb reverse tcp:8079 tcp:8079

# Then in Android app, use: http://localhost:8079/
```

---

## ✨ Summary

Your Gateway is now configured to:
1. ✓ Listen on all network interfaces (0.0.0.0:8079)
2. ✓ Handle timeouts appropriately (up to 60 seconds)
3. ✓ Allow CORS from emulator (10.0.2.2)
4. ✓ Route to all microservices via Eureka
5. ✓ Work with firewall rules

**Next Action:** Run the firewall setup script and start all services!
