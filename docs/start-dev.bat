@echo off
chcp 65001 >nul
echo ========================================
echo   黄色宠物商城 - 开发环境启动脚本
echo ========================================
echo.

REM 设置后端环境变量
echo [1/3] 设置后端环境变量...
set FILE_UPLOAD_BASE_DIR=c:/Users/rongyan/Desktop/ks/YellowPetShop/images
set FILE_URL_PREFIX=/api/images
set DB_USERNAME=root
set DB_PASSWORD=123456
set JWT_SECRET=petshop_jwt_secret_key_2026
echo ✓ 后端环境变量设置完成

echo.
echo [2/3] 启动后端服务...
cd petserver
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo [3/3] 等待5秒后启动前端...
timeout /t 5 /nobreak >nul

echo 启动前端服务...
cd vue-pet
start "前端服务" cmd /k "set VUE_APP_IMAGE_BASE_URL=http://localhost:3000 && npm run serve"
cd ..

echo.
echo ========================================
echo   启动完成！
echo   后端地址: http://localhost:3000
echo   前端地址: http://localhost:8080
echo   图片基础URL: http://localhost:3000
echo ========================================
echo.
pause
