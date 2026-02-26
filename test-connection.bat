@echo off
chcp 65001 >nul
echo ========================================
echo    SSH Connection Test
echo ========================================
echo.
echo This will test your SSH connection to the server
echo before running the full deployment.
echo.

if not exist "deploy-config.json" (
    echo [ERROR] Cannot find deploy-config.json!
    pause
    exit /b 1
)

powershell -ExecutionPolicy Bypass -File "%~dp0test-connection.ps1"

pause
