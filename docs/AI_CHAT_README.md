# AI聊天历史记录功能实现说明

## 功能概述
实现了基于Spring AI的聊天历史记录功能，支持会话管理和持久化存储。

## 数据库表结构

### 1. chat_session（会话表）
```sql
CREATE TABLE chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id)
);
```

### 2. chat_history（消息记录表）
```sql
CREATE TABLE chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    content TEXT NOT NULL,
    role VARCHAR(20) NOT NULL,
    INDEX idx_session_id (session_id),
    INDEX idx_datetime (datetime)
);
```

## 后端实现

### 核心组件

1. **实体类**
   - `ChatSession.java` - 会话实体
   - `ChatHistory.java` - 消息记录实体
   - `ChatSessionVO.java` - 会话视图对象
   - `ChatHistoryVO.java` - 消息视图对象

2. **Mapper层**
   - `ChatSessionMapper.java` - 会话数据访问
   - `ChatHistoryMapper.java` - 消息数据访问

3. **Service层**
   - `ChatSessionService.java` - 会话管理服务
   - `ChatHistoryService.java` - 消息管理服务

4. **持久化ChatMemory**
   - `DatabaseChatMemory.java` - 实现Spring AI的ChatMemory接口，基于数据库持久化

5. **Controller**
   - `ChatController.java` - 提供RESTful API

### API接口

#### 1. 创建新会话
```
POST /api/chat/session/create
Headers: token: {用户token}
Response: 会话ID
```

#### 2. 获取用户所有会话
```
GET /api/chat/session/list
Headers: token: {用户token}
Response: 会话列表
```

#### 3. 获取会话历史记录
```
GET /api/chat/history/{sessionId}
Headers: token: {用户token}
Response: 消息列表
```

#### 4. 发送消息（同步）
```
POST /api/chat/send
Headers: token: {用户token}
Params: 
  - message: 消息内容
  - sessionId: 会话ID
Response: AI回复内容
```

#### 5. 发送消息（流式）
```
GET /api/chat/sendStream
Headers: token: {用户token}
Params:
  - message: 消息内容
  - sessionId: 会话ID
Response: Server-Sent Events流
```

#### 6. 清空会话历史
```
DELETE /api/chat/history/{sessionId}
Headers: token: {用户token}
Response: 操作结果
```

## 前端实现

### 页面组件
- `AiChatView.vue` - AI助手对话页面

### 主要功能
1. **会话管理**
   - 创建新会话
   - 切换会话
   - 查看会话列表

2. **消息交互**
   - 发送消息
   - 接收AI回复
   - 查看历史记录

3. **UI特性**
   - 渐变紫色主题设计
   - 流畅的动画效果
   - 响应式布局
   - 侧边栏会话列表

### 路由配置
```javascript
{
  path: "/ai-chat",
  name: "ai-chat",
  component: () => import("../views/AiChatView.vue"),
  meta: { requiresAuth: true }
}
```

### 入口
在"我的"页面添加了"🤖 AI助手"菜单项，点击即可进入AI对话页面。

## 使用步骤

### 1. 执行数据库脚本
```bash
# 在MySQL中执行
source petserver/src/main/resources/sql/chat_tables.sql
```

### 2. 启动后端服务
确保配置了以下环境变量：
- `DEEPSEEK_API_KEY` - DeepSeek API密钥
- `DB_URL` - 数据库连接地址
- `DB_USERNAME` - 数据库用户名
- `DB_PASSWORD` - 数据库密码
- `JWT_SECRET` - JWT密钥

### 3. 启动前端服务
```bash
cd vue-pet
npm install
npm run serve
```

### 4. 使用功能
1. 登录系统
2. 进入"我的"页面
3. 点击"🤖 AI助手"
4. 开始对话

## 技术特点

1. **持久化存储** - 所有对话历史保存在数据库中
2. **会话隔离** - 每个用户的会话独立管理
3. **上下文记忆** - AI能记住最近10轮对话
4. **安全验证** - 基于JWT的用户身份验证
5. **优雅设计** - 现代化的UI界面

## 注意事项

1. 需要先创建数据库表
2. 确保DeepSeek API密钥有效
3. 前端需要用户登录后才能使用
4. 会话ID使用UUID生成，保证唯一性
