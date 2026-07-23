@echo off
chcp 65001 >nul
echo ========================================
echo    YellowPetShop 项目启动脚本
echo ========================================
echo.

:: 设置项目根目录
set PROJECT_ROOT=%~dp0
cd /d "%PROJECT_ROOT%"

:: 检查是否已有进程在运行
echo [检查] 正在检查是否有旧进程运行...
tasklist /FI "WINDOWTITLE eq 后端服务 - Spring Boot*" 2>nul | find /I /N "cmd.exe">nul
if "%ERRORLEVEL%"=="0" (
    echo [警告] 检测到后端服务可能正在运行
)

tasklist /FI "WINDOWTITLE eq 前端服务 - Vue*" 2>nul | find /I /N "cmd.exe">nul
if "%ERRORLEVEL%"=="0" (
    echo [警告] 检测到前端服务可能正在运行
)
echo.

:: 启动后端服务
echo [启动] 正在启动后端服务 (Spring Boot)...
start "后端服务 - Spring Boot" cmd /k "cd /d "%PROJECT_ROOT%petserver" && echo [后端] 正在启动 Spring Boot 应用... && mvnw.cmd spring-boot:run"
echo [完成] 后端服务已在新窗口中启动
echo.

:: 等待2秒
timeout /t 2 /nobreak >nul

:: 启动前端服务
echo [启动] 正在启动前端服务 (Vue)...
start "前端服务 - Vue" cmd /k "cd /d "%PROJECT_ROOT%vue-pet" && echo [前端] 正在启动 Vue 开发服务器... && npm run serve"
echo [完成] 前端服务已在新窗口中启动
echo.

echo ========================================
echo    启动完成！
echo ========================================
echo.
echo 后端服务: 通常运行在 http://localhost:8080
echo 前端服务: 通常运行在 http://localhost:8081
echo.
echo 提示: 
echo - 两个服务窗口已打开，请勿关闭
echo - 首次启动后端可能需要下载依赖，请耐心等待
echo - 前端启动后会自动打开浏览器
echo.
echo 按任意键关闭此窗口...
pause >nul
