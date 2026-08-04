# Quick Start Guide - Android Emulator Setup

## 📋 Prerequisites
- [ ] Windows Firewall configured (Run as Admin: `.\setup-firewall.ps1`)
- [ ] Eureka Server running on port 8761
- [ ] All microservices running and registered with Eureka
- [ ] Android Emulator running on device

---

## 🚀 Quick Start (5 Steps)

### Step 1: Setup Firewall (ONE-TIME ONLY)
```powershell
# Open PowerShell as Administrator
cd c:\Users\ck901\Desktop\locato\Gateway
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\setup-firewall.ps1
```

### Step 2: Verify Eureka Server
```bash
# Terminal 1: Make sure Eureka is running on :8761
curl http://localhost:8761
# You should see Eureka welcome page
```

### Step 3: Start All Microservices
```bash
# Terminal 2, 3, 4, 5... (separate terminal for each service)
# USERREGISTRY on :8080
# SHOP-SERVICE on :8084
# SERVICE-CHAT on :8085
# OrderService on :8086
# SuperUser on :8087
```

### Step 4: Start Gateway
```bash
# Terminal (last one or main terminal)
cd c:\Users\ck901\Desktop\locato\Gateway
mvn clean spring-boot:run
```

Wait for message: **"Started GatewayApplication"**

### Step 5: Test Connection
```bash
# In another terminal or Android emulator browser
curl http://10.0.2.2:8079/
```

---

## ✅ Verification Checklist

### Terminal Command: Test Everything
```bash
cd c:\Users\ck901\Desktop\locato\Gateway
.\test-connectivity.cmd
```

Expected output:
```
[PASS] Gateway is responding on localhost:8079
[PASS] Eureka is responding on localhost:8761
[PASS] Port 8079 is listening
[PASS] Port 8761 is listening
[PASS] Firewall rule 'Android Emulator - Microservices' exists
```

---

## 🔌 Test Endpoints

### Test from Host Machine
```bash
# Health check
curl http://localhost:8079/health

# Actuator endpoints
curl http://localhost:8079/actuator/health
curl http://localhost:8079/actuator/metrics
```

### Test from Android Emulator
In emulator browser or Postman:
```
GET http://10.0.2.2:8079/health

POST http://10.0.2.2:8079/api/auth/login
Content-Type: application/json
{
  "usernameOrEmail": "test@example.com",
  "password": "password123"
}
```

---

## 🛑 Common Issues & Quick Fixes

### Issue: Connection Timeout
```bash
# Verify Gateway is running:
netstat -ano | findstr :8079

# If not, restart Gateway:
mvn spring-boot:run
```

### Issue: "Site can't be reached"
```powershell
# Run as Administrator:
.\setup-firewall.ps1

# Then RESTART Windows
```

### Issue: Eureka not finding services
```bash
# Check Eureka dashboard:
http://localhost:8761

# All services should show as "UP"
# If not, verify their spring.application.name matches route URI
```

---

## 📱 Flutter App Configuration

In your Flutter app's HTTP client:
```dart
const baseUrl = 'http://10.0.2.2:8079';
// Emulator can access host at 10.0.2.2
```

---

## 🆘 Emergency Reset

If everything breaks:

```bash
# 1. Stop all terminals (Ctrl+C)

# 2. Kill all Java processes
taskkill /F /IM java.exe

# 3. Clear port issues
netsh int ip reset resetlog.txt

# 4. Restart Windows services
net stop WinHttpAutoProxySvc
net start WinHttpAutoProxySvc

# 5. Restart Windows (recommended)
shutdown /r /t 0

# 6. Start from Step 2 of Quick Start
```

---

## 📞 Support

**Configuration Files:**
- Gateway: `c:\Users\ck901\Desktop\locato\Gateway\src\main\resources\application.properties`
- CORS: `c:\Users\ck901\Desktop\locato\Gateway\src\main\java\com\whoami\launch\CorsConfig.java`

**Helper Scripts:**
- Setup Firewall: `.\setup-firewall.ps1`
- Start Gateway: `.\start-gateway.cmd`
- Test Connection: `.\test-connectivity.cmd`

**Documentation:**
- Full Guide: `EMULATOR_SETUP.md`
- This Guide: `QUICKSTART.md`
