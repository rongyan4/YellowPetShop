# Test SSH Connection Script
param(
    [string]$ServerHost,
    [string]$ServerUser,
    [string]$ServerPass
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   SSH Connection Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if (-not $ServerHost) {
    # Read from config file
    if (Test-Path "deploy-config.json") {
        $config = Get-Content "deploy-config.json" | ConvertFrom-Json
        $ServerHost = $config.server.host
        $ServerUser = $config.server.username
        $ServerPass = $config.server.password
    } else {
        Write-Host "Error: No config file found and no parameters provided" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Testing connection to:" -ForegroundColor Yellow
Write-Host "  Host: $ServerHost" -ForegroundColor White
Write-Host "  User: $ServerUser" -ForegroundColor White
Write-Host "  Port: 22" -ForegroundColor White
Write-Host ""

# Check if Posh-SSH is installed
if (-not (Get-Module -ListAvailable -Name Posh-SSH)) {
    Write-Host "Installing Posh-SSH module..." -ForegroundColor Yellow
    try {
        Install-Module -Name Posh-SSH -Force -Scope CurrentUser -AllowClobber
        Import-Module Posh-SSH
        Write-Host "Posh-SSH installed successfully" -ForegroundColor Green
    } catch {
        Write-Host "Error: Cannot install Posh-SSH" -ForegroundColor Red
        exit 1
    }
} else {
    Import-Module Posh-SSH
}

Write-Host ""
Write-Host "Testing SSH connection..." -ForegroundColor Cyan

try {
    # Create credentials
    $securePassword = ConvertTo-SecureString $ServerPass -AsPlainText -Force
    $credential = New-Object System.Management.Automation.PSCredential ($ServerUser, $securePassword)
    
    # Try to establish SSH session
    Write-Host "Attempting to connect..." -ForegroundColor Yellow
    $session = New-SSHSession -ComputerName $ServerHost -Credential $credential -AcceptKey -Port 22 -ConnectionTimeout 30
    
    if ($session) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "   CONNECTION SUCCESSFUL!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Session Details:" -ForegroundColor Cyan
        Write-Host "  Session ID: $($session.SessionId)" -ForegroundColor White
        Write-Host "  Host: $($session.Host)" -ForegroundColor White
        Write-Host "  Connected: $($session.Connected)" -ForegroundColor White
        Write-Host ""
        
        # Test a simple command
        Write-Host "Testing command execution..." -ForegroundColor Cyan
        $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "whoami"
        Write-Host "  Current user: $($result.Output)" -ForegroundColor White
        
        $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "pwd"
        Write-Host "  Current directory: $($result.Output)" -ForegroundColor White
        
        # Test sudo access
        Write-Host ""
        Write-Host "Testing sudo access..." -ForegroundColor Cyan
        $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo -n true 2>&1"
        if ($result.ExitStatus -eq 0) {
            Write-Host "  Sudo access: Available (passwordless)" -ForegroundColor Green
        } else {
            Write-Host "  Sudo access: May require password" -ForegroundColor Yellow
        }
        
        # Check target directories
        Write-Host ""
        Write-Host "Checking target directories..." -ForegroundColor Cyan
        
        $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "ls -ld /var/www 2>&1"
        if ($result.ExitStatus -eq 0) {
            Write-Host "  /var/www: Exists" -ForegroundColor Green
        } else {
            Write-Host "  /var/www: Not found or no permission" -ForegroundColor Yellow
        }
        
        $result = Invoke-SSHCommand -SessionId $session.SessionId -Command "ls -ld /opt 2>&1"
        if ($result.ExitStatus -eq 0) {
            Write-Host "  /opt: Exists" -ForegroundColor Green
        } else {
            Write-Host "  /opt: Not found or no permission" -ForegroundColor Yellow
        }
        
        # Close session
        Remove-SSHSession -SessionId $session.SessionId | Out-Null
        
        Write-Host ""
        Write-Host "Connection test completed successfully!" -ForegroundColor Green
        Write-Host "You can now run deploy.bat to deploy your application." -ForegroundColor Green
        
    } else {
        Write-Host "Error: Failed to create session" -ForegroundColor Red
        exit 1
    }
    
} catch {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "   CONNECTION FAILED!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible reasons:" -ForegroundColor Yellow
    Write-Host "  1. Incorrect server IP address" -ForegroundColor White
    Write-Host "  2. Incorrect username or password" -ForegroundColor White
    Write-Host "  3. SSH service not running on server" -ForegroundColor White
    Write-Host "  4. Firewall blocking port 22" -ForegroundColor White
    Write-Host "  5. Server only allows SSH key authentication" -ForegroundColor White
    Write-Host ""
    Write-Host "Troubleshooting steps:" -ForegroundColor Yellow
    Write-Host "  1. Verify server IP: $ServerHost" -ForegroundColor White
    Write-Host "  2. Try connecting manually: ssh $ServerUser@$ServerHost" -ForegroundColor White
    Write-Host "  3. Check if password authentication is enabled on server" -ForegroundColor White
    Write-Host "     Edit /etc/ssh/sshd_config and ensure:" -ForegroundColor White
    Write-Host "     PasswordAuthentication yes" -ForegroundColor White
    Write-Host "  4. Restart SSH service: sudo systemctl restart sshd" -ForegroundColor White
    Write-Host ""
    exit 1
}
