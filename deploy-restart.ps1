# PowerShell Restart Services Script
param(
    [string]$ServerHost,
    [string]$ServerUser,
    [string]$ServerPass
)

$ErrorActionPreference = "Stop"

# Read configuration
$config = Get-Content "deploy-config.json" | ConvertFrom-Json
$BackendService = $config.services.backend
$FrontendService = $config.services.frontend

Write-Host "Backend Service: $BackendService" -ForegroundColor Cyan
Write-Host "Frontend Service: $FrontendService" -ForegroundColor Cyan
Write-Host ""

# Check Posh-SSH module
if (-not (Get-Module -ListAvailable -Name Posh-SSH)) {
    Write-Host "Installing Posh-SSH module..." -ForegroundColor Yellow
    try {
        Install-Module -Name Posh-SSH -Force -Scope CurrentUser -AllowClobber
        Import-Module Posh-SSH
    } catch {
        Write-Host "Warning: Cannot install Posh-SSH, skipping service restart" -ForegroundColor Yellow
        exit 0
    }
} else {
    Import-Module Posh-SSH
}

try {
    # Create credentials
    $securePassword = ConvertTo-SecureString $ServerPass -AsPlainText -Force
    $credential = New-Object System.Management.Automation.PSCredential ($ServerUser, $securePassword)
    
    # Establish SSH session
    Write-Host "Connecting to server..." -ForegroundColor Cyan
    $session = New-SSHSession -ComputerName $ServerHost -Credential $credential -AcceptKey
    
    # Restart backend service (petshop)
    Write-Host "Restarting backend service ($BackendService)..." -ForegroundColor Cyan
    $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo systemctl restart $BackendService"
    
    if ($result.ExitStatus -eq 0) {
        Write-Host "Backend service restarted successfully" -ForegroundColor Green
    } else {
        Write-Host "Warning: Backend service restart may have failed" -ForegroundColor Yellow
        Write-Host "Error: $($result.Error)" -ForegroundColor Yellow
    }
    
    # Wait a moment
    Start-Sleep -Seconds 2
    
    # Check backend service status
    Write-Host "Checking backend service status..." -ForegroundColor Cyan
    $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo systemctl status $BackendService --no-pager"
    
    if ($result.Output -match "active \(running\)") {
        Write-Host "Backend service is running" -ForegroundColor Green
    } else {
        Write-Host "Warning: Backend service may not be running properly" -ForegroundColor Yellow
        Write-Host "Status output:" -ForegroundColor Yellow
        Write-Host $result.Output -ForegroundColor Yellow
    }
    
    Write-Host ""
    
    # Restart frontend service (nginx)
    Write-Host "Restarting frontend service ($FrontendService)..." -ForegroundColor Cyan
    $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo systemctl restart $FrontendService"
    
    if ($result.ExitStatus -eq 0) {
        Write-Host "Frontend service restarted successfully" -ForegroundColor Green
    } else {
        Write-Host "Warning: Frontend service restart may have failed" -ForegroundColor Yellow
        Write-Host "Error: $($result.Error)" -ForegroundColor Yellow
    }
    
    # Wait a moment
    Start-Sleep -Seconds 2
    
    # Check frontend service status
    Write-Host "Checking frontend service status..." -ForegroundColor Cyan
    $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo systemctl status $FrontendService --no-pager"
    
    if ($result.Output -match "active \(running\)") {
        Write-Host "Frontend service is running" -ForegroundColor Green
    } else {
        Write-Host "Warning: Frontend service may not be running properly" -ForegroundColor Yellow
        Write-Host "Status output:" -ForegroundColor Yellow
        Write-Host $result.Output -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Service restart completed!" -ForegroundColor Green
    
    # Close session
    Remove-SSHSession -SessionId $session.SessionId | Out-Null
    
    exit 0
    
} catch {
    Write-Host "Service restart failed: $_" -ForegroundColor Red
    Write-Host "Please login to server and restart services manually:" -ForegroundColor Yellow
    Write-Host "  sudo systemctl restart $BackendService" -ForegroundColor Yellow
    Write-Host "  sudo systemctl restart $FrontendService" -ForegroundColor Yellow
    exit 1
}
