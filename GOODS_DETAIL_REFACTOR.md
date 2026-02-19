# 商品详情页面修改完成文档

## 修改日期
2026-02-10

## 修改内容总结

### 1. 前端修改 (vue-pet)

#### 1.1 GoodDetailsView.vue
**删除的内容：**
- ✅ 删除"查看更多优惠"按钮（打开手机淘宝查看更多优惠）
- ✅ 删除"隔日达"配送标签和说明
- ✅ 删除"破损包退"服务标签
- ✅ 删除"生产日期"信息行
- ✅ 删除评论中的规格信息（spec字段）
- ✅ 删除整个店铺信息栏（包括店铺头像、评分、进店逛逛、全部宝贝等）
- ✅ 删除原有的底部操作栏组件（GoodDetailsTab.vue）

**修改的内容：**
- ✅ 发货地和邮费从数据库获取（`shippingOrigin` 和 `postage` 字段）
- ✅ 当 `postage` 为 0 时显示"免运费"
- ✅ 评论数据从数据库获取，包含用户头像和昵称
- ✅ 评论图片支持从后端 `comment_image` 目录加载
- ✅ 初始化评论列表为空数组，完全从后端加载
- ✅ 将"打开手机淘宝查看全部"改为"查看全部"

**新增的内容：**
- ✅ 新增底部悬浮操作栏，包含：
  - 左侧：店铺、客服、收藏三个图标按钮
  - 右侧：加入购物车（橙色）和立即购买（红色）两个按钮

**配送信息显示格式：**
```
发货地：上海    快递：免运费
```
或
```
发货地：北京    快递：¥10
```

#### 1.2 GoodDetailsNavi.vue
**修改的内容：**
- ✅ 删除"推荐"标签，只保留"宝贝"、"评价"、"详情"三个标签

#### 1.3 vue.config.js
**新增配置：**
- ✅ 添加 `/comment_image` 路径的代理映射，指向后端服务器
```javascript
'/comment_image': {
    target: 'http://0.0.0.0:3000',
    changeOrigin: true,
    pathRewrite: {
        '^/comment_image': '/comment_image'
    }
}
```

### 2. 后端修改 (petserver)

#### 2.1 WebConfig.java
**新增配置：**
- ✅ 添加评论图片静态资源映射
```java
registry.addResourceHandler("/comment_image/**")
        .addResourceLocations("classpath:/public/comment_image/");
```

### 3. 数据库结构

#### 3.1 已有的表结构
- ✅ `commodity` 表包含 `shipping_origin` 和 `postage` 字段
- ✅ `comment` 表包含评论基本信息
- ✅ `comment_image` 表存储评论图片（通过 `database_upgrade.sql` 创建）
- ✅ `user` 表包含用户头像和昵称信息

#### 3.2 评论查询逻辑
- ✅ 评论查询时关联 `user` 表获取用户信息（username, nickname, avatar）
- ✅ 评论图片通过 `comment_image` 表关联查询
- ✅ 只显示状态为 'normal' 的评论
- ✅ 支持置顶评论（is_top 字段）

### 4. 后端接口

#### 4.1 已有接口
- ✅ `GET /api/goods/detail?id={商品ID}` - 获取商品详情（包含发货地和邮费）
- ✅ `GET /api/comments/page?commodityId={商品ID}&current={页码}&size={每页数量}` - 分页获取评论
- ✅ `GET /api/comments/count?commodityId={商品ID}` - 获取评论总数

#### 4.2 CommentMapper.java
- ✅ 评论查询时关联用户表获取用户信息
```sql
SELECT c.*, u.username, u.nickname, u.avatar 
FROM comment c 
LEFT JOIN user u ON c.user_id = u.id 
WHERE c.commodity_id = #{commodityId} AND c.status = 'normal' 
ORDER BY c.is_top DESC, c.create_time DESC
```

### 5. 测试数据

#### 5.1 test_comment_data.sql
- ✅ 创建了测试用户数据（3个用户）
- ✅ 为商品 ID=6, 7, 8 添加了测试评论
- ✅ 包含评论图片插入示例（需要实际图片文件）

### 6. 需要手动操作的步骤

#### 6.1 数据库升级
```bash
# 如果还没有执行过，需要执行数据库升级脚本
mysql -u root -p pet_shop < database_upgrade.sql

# 插入测试评论数据
mysql -u root -p pet_shop < test_comment_data.sql
```

#### 6.2 创建评论图片目录
在后端项目中创建目录：
```
petserver/src/main/resources/public/comment_image/
```

#### 6.3 重启服务
- 前端：重启 Vue 开发服务器（因为修改了 vue.config.js）
- 后端：重启 Spring Boot 应用（因为修改了 WebConfig.java）

### 7. 前端显示效果

#### 7.1 价格区域
```
¥32.94
鲜活鸡蛋布丁粉1kg...
```

#### 7.2 配送信息
```
发货地：上海    快递：免运费
```

#### 7.3 服务保障
```
🔹 退货宝  极速退款  7天无理由退货
```

#### 7.4 评论显示
```
👤 爱宠小王
   非常好的商品，我家宠物很喜欢吃！质量很好，会继续回购的。
   [图片1] [图片2]
```

#### 7.5 底部操作栏
```
[店铺] [客服] [收藏]  [加入购物车] [立即购买]
```
- 左侧三个图标按钮：店铺、客服、收藏
- 右侧两个按钮：
  - 加入购物车（橙色 #ffa500）
  - 立即购买（红色 #ff6034）

### 8. 注意事项

1. **评论图片路径格式**：
   - 数据库中存储：`/comment_image/xxx.jpg`
   - 前端访问：通过代理自动转发到后端
   - 后端映射：`classpath:/public/comment_image/`

2. **邮费显示逻辑**：
   - `postage = 0` 或 `postage = 0.00` → 显示"免运费"
   - `postage > 0` → 显示"¥{金额}"

3. **评论加载**：
   - 初始加载第一页（10条）
   - 支持分页加载更多
   - 只显示状态为 'normal' 的评论

4. **用户信息显示优先级**：
   - 优先显示 `nickname`（昵称）
   - 如果没有昵称，显示 `username`（用户名）
   - 如果都没有，显示"匿名用户"

### 9. 文件修改清单

#### 前端文件
- ✅ `vue-pet/src/views/GoodDetailsView.vue` - 商品详情页面主组件
  - 删除店铺信息栏
  - 删除原有底部操作栏组件引用
  - 新增底部悬浮操作栏
  - 修改评论"查看全部"文字
- ✅ `vue-pet/src/components/good/GoodDetailsNavi.vue` - 导航栏组件
  - 删除"推荐"标签
- ✅ `vue-pet/vue.config.js` - Vue 配置文件
  - 新增评论图片代理配置

#### 后端文件
- ✅ `petserver/src/main/java/com/yellow/petshop/config/WebConfig.java` - Web配置
  - 新增评论图片静态资源映射

#### 数据库文件
- ✅ `test_comment_data.sql` - 测试评论数据（新建）

### 10. 完成状态

✅ 所有需求已完成
✅ 前后端接口已同步
✅ 数据库结构已确认
✅ 测试数据已准备

### 11. 下一步操作

1. 执行数据库脚本：
   ```bash
   mysql -u root -p pet_shop < test_comment_data.sql
   ```

2. 重启前端服务：
   ```bash
   cd vue-pet
   npm run serve
   ```

3. 重启后端服务：
   ```bash
   cd petserver
   mvn spring-boot:run
   ```

4. 访问商品详情页面测试：
   ```
   http://localhost/goods/detail?id=6
   ```

---

## 技术栈
- 前端：Vue 3 + Vant 4
- 后端：Spring Boot + MyBatis-Plus
- 数据库：MySQL 8.0
