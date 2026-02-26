@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo    Pet Shop Quick Deploy Script
echo ========================================
echo.

:: Check config file
if not exist "deploy-config.json" (
    echo [ERROR] Cannot find deploy-config.json!
    echo Please configure server information first.
    pause
    exit /b 1
)

:: Read configuration using PowerShell
echo [1/7] Reading configuration...
for /f "delims=" %%i in ('powershell -Command "(Get-Content deploy-config.json | ConvertFrom-Json).server.host"') do set SERVER_HOST=%%i
for /f "delims=" %%i in ('powershell -Command "(Get-Content deploy-config.json | ConvertFrom-Json).server.username"') do set SERVER_USER=%%i
for /f "delims=" %%i in ('powershell -Command "(Get-Content deploy-config.json | ConvertFrom-Json).server.password"') do set SERVER_PASS=%%i

if "%SERVER_HOST%"=="your-server-ip" (
    echo [ERROR] Please configure server information in deploy-config.json first!
    pause
    exit /b 1
)

echo Server: %SERVER_HOST%
echo User: %SERVER_USER%
echo.

:: Build frontend
echo [2/7] Building frontend project...
cd vue-pet
if exist "dist" rd /s /q dist
call npm run build
if errorlevel 1 (
    echo [ERROR] Frontend build failed!
    cd ..
    pause
    exit /b 1
)
cd ..
echo [SUCCESS] Frontend built to vue-pet/dist
echo.

:: Build backend
echo [3/7] Building backend project...
cd petserver
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Backend build failed!
    cd ..
    pause
    exit /b 1
)
cd ..
echo [SUCCESS] Backend built to petserver/target
echo.

:: Prepare deployment files
echo [4/7] Preparing deployment files...
if exist "deploy_temp" rd /s /q deploy_temp
mkdir deploy_temp
mkdir deploy_temp\frontend
mkdir deploy_temp\backend

:: Copy frontend build output (css, images, js, etc.)
echo Copying frontend files from vue-pet/dist...
xcopy /E /I /Y vue-pet\dist\* deploy_temp\frontend\

:: Copy backend build output (jars and classes)
echo Copying backend files from petserver/target...
xcopy /E /I /Y petserver\target\* deploy_temp\backend\

echo [SUCCESS] Files prepared
echo.

:: Upload to server
echo [5/7] Uploading files to server...
powershell -ExecutionPolicy Bypass -File "%~dp0deploy-upload.ps1" "%SERVER_HOST%" "%SERVER_USER%" "%SERVER_PASS%"

if errorlevel 1 (
    echo [ERROR] File upload failed!
    pause
    exit /b 1
)

echo [SUCCESS] Files uploaded
echo.

:: Restart services
echo [6/7] Restarting services...
powershell -ExecutionPolicy Bypass -File "%~dp0deploy-restart.ps1" "%SERVER_HOST%" "%SERVER_USER%" "%SERVER_PASS%"

if errorlevel 1 (
    echo [WARNING] Service restart may have failed, please check manually
) else (
    echo [SUCCESS] Services restarted
)
echo.

:: Clean up
echo [7/7] Cleaning up temporary files...
rd /s /q deploy_temp
echo [SUCCESS] Cleanup complete
echo.

echo ========================================
echo    Deployment Complete!
echo ========================================
echo Frontend: http://%SERVER_HOST%
echo Backend: http://%SERVER_HOST%:8080
echo.
echo Please verify services are running properly.
echo.
pause
