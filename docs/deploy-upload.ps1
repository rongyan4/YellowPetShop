# PowerShell Upload Script
param(
    [string]$ServerHost,
    [string]$ServerUser,
    [string]$ServerPass
)

$ErrorActionPreference = "Stop"

# Read configuration
$config = Get-Content "deploy-config.json" | ConvertFrom-Json
$RemoteFrontend = $config.paths.frontend.remotePath
$RemoteBackend = $config.paths.backend.remotePath

Write-Host "Remote Frontend Path: $RemoteFrontend" -ForegroundColor Cyan
Write-Host "Remote Backend Path: $RemoteBackend" -ForegroundColor Cyan
Write-Host ""

# Check for upload tools
$useWinSCP = $false
$usePSCP = $false
$usePosh = $false

# Check WinSCP
if (Test-Path "C:\Program Files (x86)\WinSCP\WinSCP.com") {
    $winscp = "C:\Program Files (x86)\WinSCP\WinSCP.com"
    $useWinSCP = $true
} elseif (Test-Path "C:\Program Files\WinSCP\WinSCP.com") {
    $winscp = "C:\Program Files\WinSCP\WinSCP.com"
    $useWinSCP = $true
}

# Check PSCP (PuTTY)
if (Get-Command pscp -ErrorAction SilentlyContinue) {
    $usePSCP = $true
}

# Check Posh-SSH module
if (Get-Module -ListAvailable -Name Posh-SSH) {
    $usePosh = $true
}

# If none available, try to install Posh-SSH
if (-not $useWinSCP -and -not $usePSCP -and -not $usePosh) {
    Write-Host "No SSH tool detected, installing Posh-SSH module..." -ForegroundColor Yellow
    try {
        Install-Module -Name Posh-SSH -Force -Scope CurrentUser -AllowClobber
        Import-Module Posh-SSH
        $usePosh = $true
        Write-Host "Posh-SSH installed successfully" -ForegroundColor Green
    } catch {
        Write-Host "Error: Cannot install Posh-SSH. Please install WinSCP or PuTTY manually." -ForegroundColor Red
        Write-Host "WinSCP download: https://winscp.net/eng/download.php" -ForegroundColor Yellow
        Write-Host "PuTTY download: https://www.putty.org/" -ForegroundColor Yellow
        exit 1
    }
}

# Upload using Posh-SSH
if ($usePosh) {
    Write-Host "Uploading files using Posh-SSH..." -ForegroundColor Cyan
    
    try {
        # Create credentials
        $securePassword = ConvertTo-SecureString $ServerPass -AsPlainText -Force
        $credential = New-Object System.Management.Automation.PSCredential ($ServerUser, $securePassword)
        
        # Establish SSH session
        Write-Host "Connecting to server $ServerHost..." -ForegroundColor Cyan
        Write-Host "Using username: $ServerUser" -ForegroundColor Cyan
        
        try {
            $session = New-SSHSession -ComputerName $ServerHost -Credential $credential -AcceptKey -Port 22
        } catch {
            Write-Host "SSH connection failed. Please check:" -ForegroundColor Red
            Write-Host "1. Server IP is correct: $ServerHost" -ForegroundColor Yellow
            Write-Host "2. Username is correct: $ServerUser" -ForegroundColor Yellow
            Write-Host "3. Password is correct" -ForegroundColor Yellow
            Write-Host "4. SSH service is running on server" -ForegroundColor Yellow
            Write-Host "5. Firewall allows SSH connections (port 22)" -ForegroundColor Yellow
            throw $_
        }
        
        # Backup old files
        Write-Host "Backing up old files..." -ForegroundColor Cyan
        $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
        Invoke-SSHCommand -SessionId $session.SessionId -Command "if [ -d '$RemoteFrontend' ]; then sudo cp -r $RemoteFrontend ${RemoteFrontend}_backup_$timestamp; fi" | Out-Null
        Invoke-SSHCommand -SessionId $session.SessionId -Command "if [ -d '$RemoteBackend' ]; then sudo cp -r $RemoteBackend ${RemoteBackend}_backup_$timestamp; fi" | Out-Null
        
        # Create/clear remote directories
        Write-Host "Preparing remote directories..." -ForegroundColor Cyan
        Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo mkdir -p $RemoteFrontend && sudo rm -rf $RemoteFrontend/*" | Out-Null
        Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo mkdir -p $RemoteBackend && sudo rm -rf $RemoteBackend/*" | Out-Null
        
        # Establish SFTP session
        Write-Host "Establishing SFTP session..." -ForegroundColor Cyan
        $sftp = New-SFTPSession -ComputerName $ServerHost -Credential $credential -AcceptKey -Port 22
        
        # Upload frontend files (css, images, js, etc.)
        Write-Host "Uploading frontend files (css, images, js, etc.)..." -ForegroundColor Cyan
        $frontendFiles = Get-ChildItem -Path "deploy_temp\frontend" -Recurse -File
        $totalFiles = $frontendFiles.Count
        $current = 0
        
        foreach ($file in $frontendFiles) {
            $current++
            $relativePath = $file.FullName.Substring((Resolve-Path "deploy_temp\frontend").Path.Length + 1)
            $remotePath = "$RemoteFrontend/$($relativePath -replace '\\', '/')"
            $remoteDir = Split-Path $remotePath -Parent
            
            # Create remote directory
            Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo mkdir -p '$remoteDir'" | Out-Null
            
            # Upload file to temp location first
            $tempPath = "/tmp/deploy_frontend_$(Split-Path $remotePath -Leaf)"
            Set-SFTPFile -SessionId $sftp.SessionId -LocalFile $file.FullName -RemotePath $tempPath -Overwrite
            
            # Move to final location with sudo
            Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo mv '$tempPath' '$remotePath'" | Out-Null
            
            $percent = [math]::Round(($current / $totalFiles) * 100)
            Write-Progress -Activity "Uploading frontend files" -Status "$current / $totalFiles" -PercentComplete $percent
        }
        Write-Progress -Activity "Uploading frontend files" -Completed
        
        # Upload backend files (jars and classes)
        Write-Host "Uploading backend files (jars, classes, etc.)..." -ForegroundColor Cyan
        $backendFiles = Get-ChildItem -Path "deploy_temp\backend" -Recurse -File
        $totalFiles = $backendFiles.Count
        $current = 0
        
        foreach ($file in $backendFiles) {
            $current++
            $relativePath = $file.FullName.Substring((Resolve-Path "deploy_temp\backend").Path.Length + 1)
            $remotePath = "$RemoteBackend/$($relativePath -replace '\\', '/')"
            $remoteDir = Split-Path $remotePath -Parent
            
            # Create remote directory
            Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo mkdir -p '$remoteDir'" | Out-Null
            
            # Upload file to temp location first
            $tempPath = "/tmp/deploy_backend_$(Split-Path $remotePath -Leaf)"
            Set-SFTPFile -SessionId $sftp.SessionId -LocalFile $file.FullName -RemotePath $tempPath -Overwrite
            
            # Move to final location with sudo
            Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo mv '$tempPath' '$remotePath'" | Out-Null
            
            $percent = [math]::Round(($current / $totalFiles) * 100)
            Write-Progress -Activity "Uploading backend files" -Status "$current / $totalFiles" -PercentComplete $percent
        }
        Write-Progress -Activity "Uploading backend files" -Completed
        
        # Set proper permissions
        Write-Host "Setting permissions..." -ForegroundColor Cyan
        Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo chown -R www-data:www-data $RemoteFrontend" | Out-Null
        Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo chmod -R 755 $RemoteFrontend" | Out-Null
        Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo chown -R root:root $RemoteBackend" | Out-Null
        Invoke-SSHCommand -SessionId $session.SessionId -Command "sudo chmod -R 755 $RemoteBackend" | Out-Null
        
        # Close sessions
        Remove-SFTPSession -SessionId $sftp.SessionId | Out-Null
        Remove-SSHSession -SessionId $session.SessionId | Out-Null
        
        Write-Host "Files uploaded successfully!" -ForegroundColor Green
        Write-Host "Frontend: $RemoteFrontend (css, images, js, etc.)" -ForegroundColor Green
        Write-Host "Backend: $RemoteBackend (jars, classes, etc.)" -ForegroundColor Green
        exit 0
        
    } catch {
        Write-Host "Upload failed: $_" -ForegroundColor Red
        exit 1
    }
}

# Upload using WinSCP
if ($useWinSCP) {
    Write-Host "Uploading files using WinSCP..." -ForegroundColor Cyan
    
    # Create WinSCP script
    $scriptContent = @"
option batch abort
option confirm off
open sftp://${ServerUser}:${ServerPass}@${ServerHost}
option transfer binary

# Backup and prepare directories
call sudo mkdir -p $RemoteFrontend
call sudo mkdir -p $RemoteBackend

# Upload frontend (css, images, js, etc.)
put deploy_temp\frontend\* $RemoteFrontend/ -preservetime

# Upload backend (jars, classes, etc.)
put deploy_temp\backend\* $RemoteBackend/ -preservetime

# Set permissions
call sudo chown -R www-data:www-data $RemoteFrontend
call sudo chmod -R 755 $RemoteFrontend
call sudo chown -R root:root $RemoteBackend
call sudo chmod -R 755 $RemoteBackend

close
exit
"@
    
    $scriptPath = "deploy_temp\winscp_script.txt"
    $scriptContent | Out-File -FilePath $scriptPath -Encoding ASCII
    
    try {
        & $winscp /script=$scriptPath
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Files uploaded successfully!" -ForegroundColor Green
            Remove-Item $scriptPath
            exit 0
        } else {
            Write-Host "Upload failed" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "Upload failed: $_" -ForegroundColor Red
        exit 1
    }
}

# Upload using PSCP
if ($usePSCP) {
    Write-Host "Uploading files using PSCP..." -ForegroundColor Cyan
    
    try {
        # Upload frontend
        Write-Host "Uploading frontend files..." -ForegroundColor Cyan
        & pscp -r -pw $ServerPass deploy_temp\frontend\* ${ServerUser}@${ServerHost}:${RemoteFrontend}/
        
        # Upload backend
        Write-Host "Uploading backend files..." -ForegroundColor Cyan
        & pscp -r -pw $ServerPass deploy_temp\backend\* ${ServerUser}@${ServerHost}:${RemoteBackend}/
        
        Write-Host "Files uploaded successfully!" -ForegroundColor Green
        exit 0
    } catch {
        Write-Host "Upload failed: $_" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Error: No upload tool available" -ForegroundColor Red
exit 1
