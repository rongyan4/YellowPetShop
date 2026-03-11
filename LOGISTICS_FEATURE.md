# 物流管理功能使用说明

## 功能概述

本次更新为订单管理系统添加了完整的物流管理功能，包括：

1. **物流信息管理**：支持为订单添加、编辑、删除物流信息
2. **订单-物流多对多关系**：一个订单可以关联多个物流记录
3. **订单状态管理**：为已发货订单添加"已送达"按钮，可将订单标记为已完成

## 数据库变更

### 新增表结构

需要执行以下SQL脚本创建物流相关表：

```sql
-- 物流信息表
CREATE TABLE IF NOT EXISTS `logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `shipping_company` VARCHAR(100) NOT NULL COMMENT '物流公司',
  `tracking_no` VARCHAR(100) NOT NULL COMMENT '物流单号',
  `status` VARCHAR(20) NOT NULL DEFAULT 'shipped' COMMENT '物流状态：shipped-已发货，delivered-已送达',
  `remark` TEXT COMMENT '备注',
  `shipping_time` DATETIME COMMENT '发货时间',
  `delivery_time` DATETIME COMMENT '送达时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_tracking_no` (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 订单物流映射表
CREATE TABLE IF NOT EXISTS `order_logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '映射ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `logistics_id` BIGINT NOT NULL COMMENT '物流ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_logistics_id` (`logistics_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流映射表';
```

SQL脚本位置：`petserver/src/main/resources/sql/logistics.sql`

## 后端实现

### 新增文件

1. **实体类**
   - `Logistics.java` - 物流信息实体
   - `OrderLogistics.java` - 订单物流映射实体
   - `LogisticsDTO.java` - 物流信息传输对象
   - `LogisticsVO.java` - 物流信息视图对象

2. **Mapper接口**
   - `LogisticsMapper.java` - 物流信息数据访问层
   - `OrderLogisticsMapper.java` - 订单物流映射数据访问层

3. **Service层**
   - `LogisticsService.java` - 物流服务接口
   - `LogisticsServiceImpl.java` - 物流服务实现

4. **Controller**
   - `LogisticsController.java` - 物流控制器

### 新增API接口

#### 物流管理接口

- `POST /api/logistics/add` - 添加物流信息
- `PUT /api/logistics/update/{logisticsId}` - 更新物流信息
- `GET /api/logistics/order/{orderId}` - 获取订单的物流信息列表
- `GET /api/logistics/detail/{logisticsId}` - 获取物流详情
- `DELETE /api/logistics/delete/{logisticsId}` - 删除物流信息

#### 订单管理接口

- `POST /api/merchant/orders/{orderId}/complete` - 标记订单为已完成

## 前端实现

### 新增页面

1. **LogisticsManagement.vue** - 物流管理页面
   - 路径：`/merchant/logistics/:id`
   - 功能：查看、添加、编辑、删除订单的物流信息

### 更新页面

1. **OrderDetail.vue** - 订单详情页面
   - 新增物流记录展示区域
   - 新增"添加物流"按钮
   - 新增"进入物流管理"按钮
   - 新增"已送达"按钮（仅已发货订单显示）

### 新增API方法

在 `merchant.js` 中新增：

```javascript
// 标记订单为已完成
export function completeOrder(orderId)

// 添加物流信息
export function addLogistics(data)

// 更新物流信息
export function updateLogistics(logisticsId, data)

// 获取订单的物流信息列表
export function getOrderLogistics(orderId)

// 获取物流详情
export function getLogisticsDetail(logisticsId)

// 删除物流信息
export function deleteLogistics(logisticsId)
```

## 使用流程

### 1. 订单发货

1. 进入订单管理页面
2. 找到已付款的订单，点击"发货"按钮
3. 填写物流公司和物流单号
4. 点击确定，订单状态变更为"已发货"

### 2. 管理物流信息

#### 方式一：在订单详情页面

1. 进入订单详情页面
2. 在"物流记录"区域点击"添加物流"按钮
3. 填写物流信息并保存
4. 可以直接编辑或删除物流记录

#### 方式二：在物流管理页面

1. 在订单详情页面点击"进入物流管理"按钮
2. 进入专门的物流管理页面
3. 可以查看所有物流记录
4. 添加、编辑、删除物流信息

### 3. 标记订单为已完成

1. 进入已发货订单的详情页面
2. 点击"已送达"按钮
3. 确认后，订单状态变更为"已完成"

## 功能特点

1. **多物流支持**：一个订单可以关联多个物流记录，适用于分批发货场景
2. **灵活管理**：支持在订单详情页快速操作，也支持进入专门页面进行详细管理
3. **状态追踪**：清晰展示物流状态和时间信息
4. **操作便捷**：提供添加、编辑、删除等完整的CRUD操作

## 注意事项

1. 首次使用前必须执行数据库迁移脚本
2. 只有已发货的订单才能标记为已完成
3. 删除物流信息会同时删除订单-物流映射关系
4. 物流单号和物流公司为必填项

## 技术栈

- **后端**：Spring Boot + MyBatis Plus
- **前端**：Vue 3 + Vant 4
- **数据库**：MySQL

## 文件清单

### 后端文件
- `petserver/src/main/java/com/yellow/petshop/model/logistics/Logistics.java`
- `petserver/src/main/java/com/yellow/petshop/model/logistics/OrderLogistics.java`
- `petserver/src/main/java/com/yellow/petshop/model/logistics/LogisticsDTO.java`
- `petserver/src/main/java/com/yellow/petshop/model/logistics/LogisticsVO.java`
- `petserver/src/main/java/com/yellow/petshop/mapper/LogisticsMapper.java`
- `petserver/src/main/java/com/yellow/petshop/mapper/OrderLogisticsMapper.java`
- `petserver/src/main/java/com/yellow/petshop/service/LogisticsService.java`
- `petserver/src/main/java/com/yellow/petshop/service/Impl/LogisticsServiceImpl.java`
- `petserver/src/main/java/com/yellow/petshop/controller/LogisticsController.java`
- `petserver/src/main/resources/sql/logistics.sql`

### 前端文件
- `vue-pet/src/views/merchant/LogisticsManagement.vue`
- `vue-pet/src/views/merchant/OrderDetail.vue` (已更新)
- `vue-pet/src/api/merchant.js` (已更新)
- `vue-pet/src/router/index.js` (已更新)
