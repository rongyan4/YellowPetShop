# 宠物商城快速部署脚本使用说明

## 📦 部署脚本说明

本项目提供了一键部署脚本，可以自动完成前端和后端的编译、上传和部署。

## 📋 文件说明

- `deploy.bat` - 主部署脚本（双击运行）
- `deploy-config.json` - 部署配置文件
- `deploy-upload.ps1` - PowerShell 上传脚本
- `deploy-restart.ps1` - PowerShell 服务重启脚本

## ⚙️ 配置步骤

### 1. 编辑 `deploy-config.json` 配置文件

```json
{
  "server": {
    "host": "your-server-ip",          // 改为你的服务器IP
    "port": 22,                         // SSH端口，默认22
    "username": "root",                 // SSH用户名
    "password": "your-password"         // SSH密码
  },
  "paths": {
    "frontend": {
      "localBuildDir": "./vue-pet/dist",
      "remotePath": "/var/www/petshop"  // 前端部署路径（包含css, images等）
    },
    "backend": {
      "localBuildDir": "./petserver/target",
      "remotePath": "/opt/petshop"      // 后端部署路径（包含jar, classes等）
    }
  },
  "services": {
    "backend": "petshop",               // 后端服务名
    "frontend": "nginx"                 // 前端服务名
  }
}
```

### 2. 配置示例

假设你的服务器信息如下：
- IP: 192.168.1.100
- 用户名: root
- 密码: MyPassword123

修改配置：

```json
{
  "server": {
    "host": "192.168.1.100",
    "port": 22,
    "username": "root",
    "password": "MyPassword123"
  },
  "paths": {
    "frontend": {
      "localBuildDir": "./vue-pet/dist",
      "remotePath": "/var/www/petshop"
    },
    "backend": {
      "localBuildDir": "./petserver/target",
      "remotePath": "/opt/petshop"
    }
  },
  "services": {
    "backend": "petshop",
    "frontend": "nginx"
  }
}
```

## 🚀 使用方法

### 方法一：双击运行（推荐）

1. 配置好 `deploy-config.json`
2. 双击 `deploy.bat` 文件
3. 等待部署完成

### 方法二：命令行运行

```cmd
deploy.bat
```

## 📝 部署流程

脚本会自动执行以下步骤：

1. ✅ 读取配置文件
2. ✅ 编译前端项目（npm run build）→ 输出到 `vue-pet/dist`（包含 css, images, js 等）
3. ✅ 编译后端项目（Maven打包）→ 输出到 `petserver/target`（包含 jar, classes 等）
4. ✅ 准备上传文件
5. ✅ 上传文件到服务器
   - 前端文件 → `/var/www/petshop/`（包含 css, images 等文件夹）
   - 后端文件 → `/opt/petshop/`（包含 jar, classes 等）
6. ✅ 重启服务
   - 后端：`systemctl restart petshop`
   - 前端：`systemctl restart nginx`
7. ✅ 清理临时文件

## 🗂️ 目录结构说明

### 本地编译输出

**前端（vue-pet/dist）：**
```
vue-pet/dist/
├── css/           # 样式文件
├── images/        # 图片资源
├── js/            # JavaScript文件
├── index.html     # 入口页面
└── ...
```

**后端（petserver/target）：**
```
petserver/target/
├── classes/       # 编译后的class文件
├── *.jar          # 打包的jar文件
└── ...
```

### 服务器部署目录

**前端（/var/www/petshop）：**
```
/var/www/petshop/
├── css/           # 样式文件
├── images/        # 图片资源
├── js/            # JavaScript文件
├── index.html     # 入口页面
└── ...
```

**后端（/opt/petshop）：**
```
/opt/petshop/
├── classes/       # 编译后的class文件
├── *.jar          # 打包的jar文件
└── ...
```

## 🔧 依赖工具

脚本会自动检测并使用以下工具之一：

1. **Posh-SSH**（推荐）- 脚本会自动安装
2. **WinSCP** - 如已安装会自动使用
3. **PuTTY (PSCP)** - 如已安装会自动使用

如果没有安装任何工具，脚本会自动尝试安装 Posh-SSH 模块。

### 手动安装工具（可选）

如果自动安装失败，可以手动安装：

**安装 Posh-SSH：**
```powershell
Install-Module -Name Posh-SSH -Force -Scope CurrentUser
```

**或下载 WinSCP：**
https://winscp.net/eng/download.php

**或下载 PuTTY：**
https://www.putty.org/

## ⚠️ 注意事项

1. **首次运行**：首次运行时可能需要安装 Posh-SSH 模块，请允许安装
2. **网络连接**：确保本地可以访问服务器的 SSH 端口（默认22）
3. **服务器权限**：确保 SSH 用户有 sudo 权限
4. **防火墙**：确保服务器防火墙允许 SSH 连接
5. **备份**：脚本会自动备份旧文件，备份文件名带时间戳

## 📞 服务器端配置

### 1. 创建部署目录

```bash
sudo mkdir -p /var/www/petshop
sudo mkdir -p /opt/petshop
```

### 2. 配置 Nginx（前端）

编辑 Nginx 配置：

```bash
sudo nano /etc/nginx/sites-available/petshop
```

内容：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/petshop;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

启用配置：

```bash
sudo ln -s /etc/nginx/sites-available/petshop /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 3. 配置后端服务（systemd）

创建服务文件：

```bash
sudo nano /etc/systemd/system/petshop.service
```

内容：

```ini
[Unit]
Description=Pet Shop Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/petshop
ExecStart=/usr/bin/java -jar /opt/petshop/petserver.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用服务：

```bash
sudo systemctl daemon-reload
sudo systemctl enable petshop
sudo systemctl start petshop
```

### 4. 验证服务状态

```bash
# 检查后端服务
sudo systemctl status petshop

# 检查前端服务
sudo systemctl status nginx
```

## 🐛 故障排查

### 问题1：编译失败

**前端编译失败：**
```cmd
cd vue-pet
npm install
npm run build
```

**后端编译失败：**
```cmd
cd petserver
mvnw.cmd clean package
```

### 问题2：上传失败

- 检查服务器 IP、用户名、密码是否正确
- 检查网络连接
- 检查防火墙设置
- 尝试手动 SSH 连接测试

### 问题3：服务重启失败

手动登录服务器重启：

```bash
# 重启后端服务
sudo systemctl restart petshop
sudo systemctl status petshop

# 重启前端服务
sudo systemctl restart nginx
sudo systemctl status nginx

# 查看日志
sudo journalctl -u petshop -f
sudo journalctl -u nginx -f
```

### 问题4：权限问题

如果遇到权限问题：

```bash
# 设置前端目录权限
sudo chown -R www-data:www-data /var/www/petshop
sudo chmod -R 755 /var/www/petshop

# 设置后端目录权限
sudo chown -R root:root /opt/petshop
sudo chmod -R 755 /opt/petshop
```

## 🔒 安全建议

1. **不要提交密码**：不要将包含真实密码的 `deploy-config.json` 提交到 Git
2. **使用密钥**：建议使用 SSH 私钥代替密码
3. **限制权限**：为部署创建专用的 SSH 用户，限制其权限

## 🎯 快速开始

1. 编辑 `deploy-config.json`，填写服务器信息
2. 双击 `deploy.bat`
3. 等待部署完成
4. 访问你的网站

就这么简单！🎉

## 📊 部署日志

部署过程中的所有输出都会显示在命令行窗口中。

服务器端日志位置：
- 后端服务：`sudo journalctl -u petshop -f`
- 前端服务：`sudo journalctl -u nginx -f`
