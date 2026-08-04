# PowerShell Script to Configure Windows Firewall for Android Emulator Connectivity
# Run as Administrator

# Colors for output
$Success = "Green"
$Warning = "Yellow"
$Error = "Red"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Android Emulator Firewall Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check if running as administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")

if (-not $isAdmin) {
    Write-Host "ERROR: This script must be run as Administrator!" -ForegroundColor $Error
    exit 1
}

Write-Host "✓ Running as Administrator" -ForegroundColor $Success

# Ports to allow
$ports = @(8079, 8080, 8084, 8085, 8086, 8761)
$ruleName = "Android Emulator - Microservices"

# Check if rule already exists
$existingRule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue

if ($existingRule) {
    Write-Host "Rule '$ruleName' already exists. Removing..." -ForegroundColor $Warning
    Remove-NetFirewallRule -DisplayName $ruleName -Confirm:$false
}

# Create new rule
Write-Host "Creating firewall rule for ports: $($ports -join ', ')..." -ForegroundColor Cyan

$portString = $ports -join ","

New-NetFirewallRule `
    -DisplayName $ruleName `
    -Direction Inbound `
    -Action Allow `
    -Protocol TCP `
    -LocalPort $portString `
    -Profile @("Private", "Domain") `
    -Description "Allow Android Emulator to connect to Spring Boot Microservices"

Write-Host "✓ Firewall rule created successfully!" -ForegroundColor $Success

# Display rule details
Write-Host "`nRule Details:" -ForegroundColor Cyan
Get-NetFirewallRule -DisplayName $ruleName | Select-Object DisplayName, Direction, Action, Enabled

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "✓ Firewall Configuration Complete!" -ForegroundColor $Success
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nNext Steps:" -ForegroundColor Cyan
Write-Host "1. Start Eureka Service (port 8761)"
Write-Host "2. Start all Microservices (ports 8080, 8084, 8085, 8086)"
Write-Host "3. Start Gateway Service (port 8079)"
Write-Host "4. Test from emulator: http://10.0.2.2:8079/"
Write-Host "`n"
