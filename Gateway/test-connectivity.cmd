@echo off
REM Quick Connectivity Test Script

setlocal enabledelayedexpansion

echo.
echo ========================================
echo  Gateway Connectivity Test
echo ========================================
echo.

REM Test 1: Check if Gateway is running
echo [TEST 1] Checking Gateway on localhost:8079...
curl -s http://localhost:8079/ >nul 2>&1
if %errorlevel% equ 0 (
    echo [PASS] Gateway is responding on localhost:8079
) else (
    echo [FAIL] Gateway not responding on localhost:8079
    echo        Start Gateway with: mvn spring-boot:run
)

echo.

REM Test 2: Check if Eureka is running
echo [TEST 2] Checking Eureka Service on localhost:8761...
curl -s http://localhost:8761/ >nul 2>&1
if %errorlevel% equ 0 (
    echo [PASS] Eureka is responding on localhost:8761
) else (
    echo [FAIL] Eureka not responding on localhost:8761
    echo        Make sure Eureka Server is running
)

echo.

REM Test 3: Check if ports are listening
echo [TEST 3] Checking listening ports...
echo Checking port 8079 (Gateway)...
netstat -ano | findstr :8079 >nul 2>&1
if %errorlevel% equ 0 (
    echo [PASS] Port 8079 is listening
) else (
    echo [FAIL] Port 8079 is not listening
)

echo Checking port 8761 (Eureka)...
netstat -ano | findstr :8761 >nul 2>&1
if %errorlevel% equ 0 (
    echo [PASS] Port 8761 is listening
) else (
    echo [FAIL] Port 8761 is not listening
)

echo.

REM Test 4: Check firewall rule
echo [TEST 4] Checking Firewall Rules...
powershell -Command "Get-NetFirewallRule -DisplayName 'Android Emulator - Microservices' -ErrorAction SilentlyContinue" >nul 2>&1
if %errorlevel% equ 0 (
    echo [PASS] Firewall rule 'Android Emulator - Microservices' exists
) else (
    echo [FAIL] Firewall rule not found
    echo        Run PowerShell as Admin: .\setup-firewall.ps1
)

echo.
echo ========================================
echo  Test Complete
echo ========================================
echo.

pause
