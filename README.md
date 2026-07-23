<p align="center">
  <img src="https://img.shields.io/badge/版本-2.0.0-blue.svg" alt="版本">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.2.13-4FC08D.svg" alt="Vue 3">
  <img src="https://img.shields.io/badge/Vant-4.x-07C160.svg" alt="Vant 4">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.9-red.svg" alt="MyBatis-Plus">
  <img src="https://img.shields.io/badge/License-AGPL--3.0%20%7C%20Commercial-blue.svg" alt="License">
  <img src="https://img.shields.io/badge/Java-17-ED8B00.svg" alt="Java 17">
  <img src="https://img.shields.io/badge/Node-%3E%3D22.14-339933.svg" alt="Node">
  <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg" alt="PRs Welcome">
</p>

<h1 align="center">🐾 大黄宠物商城 — YellowPetShop</h1>

<p align="center">
  <strong>全功能宠物用品电商平台 · 买家端 H5 + 商家端管理后台 · 集成 AI 智能客服</strong>
</p>

<p align="center">
  <a href="#-项目简介">项目简介</a> ·
  <a href="#-功能全景">功能全景</a> ·
  <a href="#-技术栈">技术栈</a> ·
  <a href="#-快速开始">快速开始</a> ·
  <a href="#-项目结构">项目结构</a> ·
  <a href="#-api-文档">API 文档</a> ·
  <a href="#-部署">部署</a> ·
  <a href="#-开源协议">开源协议</a>
</p>

---

## 📖 项目简介

**大黄宠物商城** 是一个面向宠物主与宠物商家的 **B2C + B2B 电商平台**，采用前后端分离架构。系统同时覆盖 **移动端买家入口**（H5 触屏版）与 **商家端管理后台**（PC 响应式），并集成了基于 DeepSeek 大模型的 **AI 智能客服助手**，为用户提供 7×24 小时的购物咨询与售后服务。

> 🌐 线上演示：[暂未开放](https://github.com/rongyan4/YellowPetShop)

---

## ✨ 功能全景

### 🛍️ 买家端（C 端）

| 模块 | 功能 |
|------|------|
| **首页** | 轮播广告位、商品推荐流、分类快捷入口 |
| **商品中心** | 多级分类导航、关键词搜索、商品详情、多图展示 |
| **购物车** | 增删改查、数量调整、选中结算、全选操作 |
| **订单系统** | 下单确认、订单列表/详情、超时自动取消（定时任务）、订单状态跟踪 |
| **支付系统** | 钱包余额支付、交易流水记录 |
| **用户中心** | 注册/登录（JWT 双令牌机制）、个人资料编辑、账号管理 |
| **收货地址** | 地址 CRUD、省市区级联选择、默认地址设置 |
| **评价系统** | 商品图文评价、商家回复、评价列表 |
| **收藏与足迹** | 商品收藏、浏览历史记录 |
| **钱包系统** | 余额查询、交易明细、安全验证 |
| **宠物档案** | 多宠物管理（头像、品种、年龄、体重） |
| **🤖 AI 智能助手** | 基于 DeepSeek 的流式对话客服，10 轮上下文记忆 |

### 🏪 商家端（B 端）

| 模块 | 功能 |
|------|------|
| **仪表盘** | 销售数据概览、关键指标统计 |
| **商品管理** | 商品发布/编辑、上下架管理、多规格 SKU |
| **分类管理** | 商品分类增删改、级联结构维护 |
| **订单管理** | 订单列表/详情、订单发货、改价操作 |
| **会员管理** | 会员列表、会员详情查询 |
| **物流管理** | 物流公司配置、运单管理 |
| **评价管理** | 商品评价查看、商家回复 |

---

## 🛠️ 技术栈

### 前端

| 技术 | 说明 |
|------|------|
| **Vue 3** (Composition API) | 渐进式前端框架 |
| **Vant 4** | 轻量、可靠的移动端 UI 组件库 |
| **Pinia + Vuex** | 状态管理（Pinia 为主，Vuex 兼容） |
| **Vue Router 4** | 前端路由（含路由守卫鉴权） |
| **Axios** | HTTP 客户端（自动 Token 刷新、拦截器） |
| **Sass/SCSS** | CSS 预处理器 |
| **Vue CLI 5** | 项目脚手架与构建工具 |

### 后端

| 技术 | 版本 |
|------|------|
| **Spring Boot** | 3.5.8 |
| **Java** | 17 |
| **MyBatis-Plus** | 3.5.9 |
| **MySQL** | 8.x |
| **Spring AI + DeepSeek** | 1.0.0 BOM |
| **JWT (jjwt)** | 0.11.5 |
| **BCrypt (jbcrypt)** | 0.4 |
| **阿里云 OSS** | 3.17.4 |
| **SpringDoc OpenAPI** | 2.8.15 |
| **Jsoup (XSS 过滤)** | 1.17.2 |

### 架构特色

- **双令牌鉴权机制**：Access Token + Refresh Token，HttpOnly Cookie 存储，有效防范 XSS 攻击
- **双角色认证**：用户端与商家端各自独立 JWT 拦截器，权限完全隔离
- **流式 AI 对话**：基于 SSE（Server-Sent Events）实现逐字流式输出，对话体验流畅
- **双模式文件存储**：本地文件系统 + 阿里云 OSS 可配置切换
- **订单超时自动取消**：基于 Spring 定时任务的订单超时处理
- **XSS 防护**：全局 HTML 内容过滤，保障用户输入安全

---

## 🚀 快速开始

### 环境要求

- **Node.js** >= 22.14.0
- **Java** >= 17
- **Maven** >= 3.8
- **MySQL** >= 8.0

### 1️⃣ 克隆项目

```bash
git clone https://github.com/your-username/YellowPetShop.git
cd YellowPetShop
```

### 2️⃣ 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE petshop DEFAULT CHARACTER SET utf8mb4;"

# 导入数据表结构与基础数据
mysql -u root -p petshop < petshop.sql
```

### 3️⃣ 后端启动

```bash
cd petserver

# 复制环境变量模板并编辑
cp .env.example .env
# 编辑 .env，配置数据库连接、JWT密钥、DeepSeek API Key 等

# 编译运行
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

后端服务默认启动在 `http://localhost:3000`，API 文档地址 `http://localhost:3000/swagger-ui.html`。

### 4️⃣ 前端启动

```bash
cd vue-pet

# 安装依赖
npm install

# 开发服务器启动（热更新）
npm run serve
```

前端 dev server 默认启动在 `http://localhost:8080`，已配置代理转发 `/api` 请求到后端。

### 5️⃣ 构建部署

```bash
# 前端构建
cd vue-pet
npm run build    # 输出到 dist/ 目录

# 后端构建
cd petserver
mvn clean package -DskipTests    # 输出 jar 包到 target/
```

---

## 📁 项目结构

```
YellowPetShop/
├── vue-pet/                          # 🎨 前端项目 (Vue 3 + Vant)
│   ├── src/
│   │   ├── api/                      #   API 接口封装（Axios）
│   │   ├── views/                    #   页面视图
│   │   │   ├── merchant/             #     商家端管理页面
│   │   │   ├── pet/                  #     宠物档案模块
│   │   │   └── ...                   #     买家端页面
│   │   ├── components/               #   公共组件
│   │   ├── stores/                   #   Pinia 状态管理
│   │   ├── router/                   #   路由配置（含守卫）
│   │   ├── utils/                    #   工具函数
│   │   └── assets/                   #   静态资源
│   ├── vue.config.js                 #   CLI 配置 / 代理
│   └── package.json
│
├── petserver/                        # ⚙️ 后端项目 (Spring Boot)
│   ├── src/main/java/com/yellow/petshop/
│   │   ├── config/                   #   配置类
│   │   ├── controller/               #   控制器 (REST API)
│   │   ├── service/                  #   业务逻辑层
│   │   ├── mapper/                   #   MyBatis-Plus Mapper
│   │   ├── model/                    #   数据模型 (Entity/VO/DTO)
│   │   ├── interceptor/              #   JWT 拦截器
│   │   ├── exception/                #   全局异常处理
│   │   ├── task/                     #   定时任务
│   │   └── util/                     #   工具类
│   ├── src/main/resources/
│   │   ├── application.yml           #   主配置文件
│   │   ├── prompt/                   #   AI 系统提示词
│   │   └── sql/                      #   额外 SQL 脚本
│   └── pom.xml
│
├── docs/                             # 📚 项目文档
├── images/                           # 🖼️ 图片资源目录
├── petshop.sql                       # 💾 完整数据库脚本
└── .env                              # 🔐 环境变量配置
```

---

## 📚 API 文档

项目内置基于 **SpringDoc OpenAPI** 的接口文档，启动后端服务后访问：

- **Swagger UI**：[`http://localhost:3000/swagger-ui.html`](http://localhost:3000/swagger-ui.html)
- **OpenAPI JSON**：[`http://localhost:3000/v3/api-docs`](http://localhost:3000/v3/api-docs)

API 按模块组织，涵盖 20+ 控制器，支持在线调试和交互式测试。

---

## 🤖 AI 智能客服

大黄宠物商城集成 **DeepSeek 大模型**，提供智能购物助手服务：

- **流式回复**：基于 SSE 实现打字机效果，对话体验流畅自然
- **上下文记忆**：保留最近 10 轮对话记忆，理解连贯
- **知识库集成**：预设系统提示词，精准回答宠物商品相关问题
- **XSS 防护**：对所有 AI 生成内容进行安全过滤，保障用户安全

---

## ☁️ 部署

### 生产环境架构

```
客户端（浏览器）
    ↕
Nginx（反向代理 + 静态资源托管）
    ↕
Spring Boot（API Server :3000）
    ↕
MySQL（数据库）
```

### 一键部署脚本

项目提供 Windows 一键部署脚本：

```bash
# 修改 deploy-config.json 中的服务器信息
# 运行一键部署
.\deploy.bat
```

部署脚本自动完成：前端构建 → 后端打包 → SCP 上传 → SSH 远程重启。

---

## 🤝 贡献指南

欢迎提交 Pull Request 或 Issue 来改进大黄宠物商城！

1. Fork 本仓库
2. 创建特性分支（`git checkout -b feature/amazing-feature`）
3. 提交更改（`git commit -m 'feat: 添加某个特性'`）
4. 推送到分支（`git push origin feature/amazing-feature`）
5. 打开 Pull Request

提交前请确保代码风格一致，并尽量附带相关测试。

---

## 📄 开源协议

本项目采用 **双授权模式（Dual-License）**，在保护开源精神的同时保障作者的合法权益。

### 一、开源授权：AGPL-3.0

```
版权所有 © 2025-2026 rongyan4

本程序为自由软件，在遵照自由软件基金会发布的 GNU Affero 通用公共许可协议
（GNU Affero General Public License）第三版的条款下，
您可以重新分发和/或修改它。

这意味着：
✅ 个人学习、研究、非商业用途 — 完全免费
✅ 允许修改和分发 — 但必须在相同协议下开源
✅ 网络提供服务必须开放源代码（AGPL 核心条款）
✅ 保留完整的版权声明
```

> **传染性说明**：AGPL-3.0 是强 Copyleft（传染性）协议。任何基于本项目的修改、衍生作品，必须以 AGPL-3.0 协议发布，不得闭源或转为专有软件。通过网络提供服务时也必须提供完整源代码。

### 二、商业授权

**任何形式的商业使用**（包括但不限于：线上运营盈利、商业公司内部使用、作为商业产品或其组成部分、提供 SaaS 服务等）**需联系作者进行商业授权**。

商业授权包含：
- 闭源使用的合法权利
- 免除 AGPL-3.0 的"网络服务须开源"义务
- 无用户数限制
- 提供优先技术支持选项

---

<p align="center">
  <sub>Built with ❤️ for the open source community</sub>
  <br>
  <sub>🐱 🐶 🐰 🐹 🐠 🦜</sub>
</p>
