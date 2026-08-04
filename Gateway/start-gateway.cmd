# Batch script to verify Gateway connectivity and startup
# This script checks all prerequisites and runs the Gateway

@echo off
setlocal enabledelayedexpansion

echo.
echo ========================================
echo  Spring Boot Gateway - Startup Verify
echo ========================================
echo.

REM Colors (simulated with text)
echo [INFO] Checking Prerequisites...

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven not found. Please install Maven or add it to PATH.
    exit /b 1
)
echo [OK] Maven is installed

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Please install Java or add it to PATH.
    exit /b 1
)
echo [OK] Java is installed

REM Check if Eureka is running
echo.
echo [INFO] Checking Eureka Service at http://localhost:8761...
curl -s http://localhost:8761 >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] Eureka Service not responding at http://localhost:8761
    echo [INFO] Make sure Eureka Server is running before starting Gateway
) else (
    echo [OK] Eureka Service is accessible
)

echo.
echo ========================================
echo  Starting Spring Boot Gateway...
echo ========================================
echo.
echo Configuration:
echo   - Port: 8079
echo   - Address: 0.0.0.0 (all interfaces)
echo   - Service Discovery: Eureka (http://localhost:8761)
echo   - Emulator Access: http://10.0.2.2:8079/
echo.

REM Start the Gateway
mvn clean spring-boot:run -DskipTests

pause
