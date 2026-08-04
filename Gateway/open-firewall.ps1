# Run as Administrator - Opens firewall for Android Emulator
# Right-click PowerShell and select "Run as administrator" before running this script

Write-Host "Opening Windows Firewall for Android Emulator on port 8079..."

# Remove existing rule if it exists
Get-NetFirewallRule -DisplayName "Allow Gateway 8079" -ErrorAction SilentlyContinue | Remove-NetFirewallRule -Confirm:$false

# Create new firewall rule for port 8079
New-NetFirewallRule `
  -DisplayName "Allow Gateway 8079" `
  -Direction Inbound `
  -Protocol TCP `
  -LocalPort 8079 `
  -Action Allow `
  -Profile Any `
  -RemoteAddress Any `
  -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "[SUCCESS] Firewall rule created successfully!"
Write-Host "[SUCCESS] Port 8079 is now open for Android Emulator"
Write-Host ""
Write-Host "Android Emulator can connect using:"
Write-Host "http://10.0.2.2:8079"
Write-Host ""

# Show local machine IP automatically
$ip = (Get-NetIPAddress -AddressFamily IPv4 |
       Where-Object {
           $_.IPAddress -notlike "127.*" -and
           $_.IPAddress -notlike "169.254.*"
       } |
       Select-Object -First 1 -ExpandProperty IPAddress)

if ($ip) {
    Write-Host "Alternative IP:"
    Write-Host "http://$ip`:8079"
}

Write-Host ""
pause