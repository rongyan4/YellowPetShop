#!/bin/bash

echo "========================================"
echo "  黄色宠物商城 - 开发环境启动脚本"
echo "========================================"
echo ""

# 设置后端环境变量
echo "[1/3] 设置后端环境变量..."
export FILE_UPLOAD_BASE_DIR="c:/Users/rongyan/Desktop/ks/YellowPetShop/images"
export FILE_URL_PREFIX="/api/images"
export DB_USERNAME="root"
export DB_PASSWORD="123456"
export JWT_SECRET="petshop_jwt_secret_key_2026"
echo "✓ 后端环境变量设置完成"

echo ""
echo "[2/3] 启动后端服务..."
cd petserver
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo ""
echo "[3/3] 等待5秒后启动前端..."
sleep 5

echo "启动前端服务..."
cd vue-pet
VUE_APP_IMAGE_BASE_URL=http://localhost:3000 npm run serve &
FRONTEND_PID=$!
cd ..

echo ""
echo "========================================"
echo "  启动完成！"
echo "  后端地址: http://localhost:3000"
echo "  前端地址: http://localhost:8080"
echo "  图片基础URL: http://localhost:3000"
echo "  后端PID: $BACKEND_PID"
echo "  前端PID: $FRONTEND_PID"
echo "========================================"
echo ""
echo "按 Ctrl+C 停止服务"

# 等待用户中断
wait
