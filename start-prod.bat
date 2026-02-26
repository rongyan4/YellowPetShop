@echo off
chcp 65001 >nul
echo ========================================
echo   黄色宠物商城 - 生产环境启动脚本
echo ========================================
echo.

REM 设置生产环境变量（请根据实际情况修改）
echo [1/2] 设置生产环境变量...
set FILE_UPLOAD_BASE_DIR=/var/www/petshop/images
set FILE_URL_PREFIX=/api/images
set DB_USERNAME=your_production_db_user
set DB_PASSWORD=your_production_db_password
set JWT_SECRET=your_production_jwt_secret_key
echo ✓ 生产环境变量设置完成

echo.
echo [2/2] 启动后端服务...
cd petserver
java -jar target/petserver-0.0.1-SNAPSHOT.jar

echo.
echo ========================================
echo   生产环境启动完成！
echo   注意：前端需要配置 VUE_APP_IMAGE_BASE_URL
echo ========================================
pause
