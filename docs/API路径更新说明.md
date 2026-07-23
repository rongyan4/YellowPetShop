# API路径更新说明

## 商家端订单管理API路径变更

### 旧路径 → 新路径

| 功能 | 旧路径 | 新路径 | 方法 | 说明 |
|------|--------|--------|------|------|
| 订单列表 | `/api/merchant/order/list` | `/merchant/orders` | GET | 已更新 |
| 订单详情 | `/api/merchant/order/detail/{id}` | `/merchant/orders/{id}` | GET | 已更新 |
| 修改价格 | `/api/merchant/order/price` | `/merchant/orders/update-price` | POST | 已更新，改为POST |
| 发货 | `/api/merchant/order/shipping` | `/merchant/orders/ship` | POST | 已更新，改为POST |
| 取消订单 | `/api/merchant/order/cancel` | 暂未实现 | - | 待实现 |

## 新增的商家端API

### 数据概览
- `GET /merchant/dashboard` - 获取商家数据概览

### 商品管理
- `POST /merchant/products/{id}/status?isValid=true` - 商品上下架

### 评论管理
- `GET /merchant/products/{id}/comments` - 获取商品评论列表
- `POST /merchant/comments/reply` - 回复评论
- `DELETE /merchant/comments/{id}` - 删除评论
- `POST /merchant/comments/{id}/top?isTop=true` - 置顶评论

## 客户端新增API

### 评论功能
- `POST /api/comments/create` - 创建评论

## 前端API文件

### merchant.js（新）
包含所有新的商家端API接口，推荐使用。

### merchantOrder.js（已更新）
已更新为使用新的API路径，保持向后兼容。

### merchantGoods.js
商品管理API（如果存在）。

### comment.js（已更新）
添加了评论创建接口。

## 使用建议

1. **新项目**：直接使用 `merchant.js` 中的API
2. **现有项目**：`merchantOrder.js` 已更新，无需修改调用代码
3. **参数变化**：
   - 改价和发货接口从 `params` 改为 `data`（使用POST请求体）
   - 订单列表的参数名保持不变

## 请求参数示例

### 订单列表
```javascript
getMerchantOrderList({
  page: 1,
  size: 10,
  status: 'pending' // 可选
})
```

### 订单改价
```javascript
updateOrderPrice({
  orderId: 1,
  newTotalAmount: 100.00,
  newPostage: 10.00,
  reason: '优惠活动'
})
```

### 订单发货
```javascript
shipOrder({
  orderId: 1,
  shippingCompany: '顺丰速运',
  trackingNo: 'SF1234567890',
  remark: '已发货'
})
```

## 注意事项

1. 所有商家端接口需要在请求头中携带 `Authorization: Bearer {token}`
2. 订单改价只能对待支付（pending）状态的订单
3. 订单发货只能对已付款（paid）状态的订单
4. 发货后会自动更新商品销量
