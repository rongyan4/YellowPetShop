@echo off
chcp 65001 >nul
echo ========================================
echo    YellowPetShop 项目停止脚本
echo ========================================
echo.

echo [停止] 正在查找并停止服务...
echo.

:: 停止 Node.js 进程 (Vue前端)
echo [前端] 正在停止 Vue 开发服务器...
taskkill /F /FI "WINDOWTITLE eq 前端服务 - Vue*" 2>nul
if "%ERRORLEVEL%"=="0" (
    echo [完成] 前端服务已停止
) else (
    echo [提示] 未找到运行中的前端服务
)
echo.

:: 停止 Java 进程 (Spring Boot后端)
echo [后端] 正在停止 Spring Boot 应用...
taskkill /F /FI "WINDOWTITLE eq 后端服务 - Spring Boot*" 2>nul
if "%ERRORLEVEL%"=="0" (
    echo [完成] 后端服务已停止
) else (
    echo [提示] 未找到运行中的后端服务
)
echo.

:: 额外清理：强制停止可能残留的进程
echo [清理] 正在清理可能残留的进程...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| find "PID:"') do (
    tasklist /FI "PID eq %%i" /V | find "spring-boot" >nul
    if not errorlevel 1 (
        taskkill /F /PID %%i >nul 2>&1
        echo [清理] 已停止 Java 进程 (PID: %%i)
    )
)

for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq node.exe" /FO LIST ^| find "PID:"') do (
    tasklist /FI "PID eq %%i" /V | find "vue-cli-service" >nul
    if not errorlevel 1 (
        taskkill /F /PID %%i >nul 2>&1
        echo [清理] 已停止 Node 进程 (PID: %%i)
    )
)
echo.

echo ========================================
echo    所有服务已停止
echo ========================================
echo.
echo 按任意键关闭此窗口...
pause >nul
