import request from '@/utils/request';

/**
 * 商家登录
 */
export function merchantLogin(data) {
  return request({
    url: '/merchant/login',
    method: 'post',
    data
  });
}

/**
 * 获取商家信息
 */
export function getMerchantInfo() {
  return request({
    url: '/merchant/info',
    method: 'get'
  });
}

/**
 * 修改商家密码
 */
export function updateMerchantPassword(data) {
  return request({
    url: '/merchant/password',
    method: 'post',
    data
  });
}

/**
 * 获取数据概览
 */
export function getDashboard() {
  return request({
    url: '/merchant/dashboard',
    method: 'get'
  });
}

/**
 * 分页查询订单列表
 */
export function getOrderList(params) {
  return request({
    url: '/merchant/orders',
    method: 'get',
    params
  });
}

/**
 * 获取订单详情
 */
export function getOrderDetail(orderId) {
  return request({
    url: `/merchant/orders/${orderId}`,
    method: 'get'
  });
}

/**
 * 修改待支付订单价格
 */
export function updateOrderPrice(data) {
  return request({
    url: '/merchant/orders/update-price',
    method: 'post',
    data
  });
}

/**
 * 发货
 */
export function shipOrder(data) {
  return request({
    url: '/merchant/orders/ship',
    method: 'post',
    data
  });
}

/**
 * 更新商品上下架状态
 */
export function updateProductStatus(productId, isValid) {
  return request({
    url: `/merchant/products/${productId}/status`,
    method: 'post',
    params: { isValid }
  });
}

/**
 * 获取商品评论列表
 */
export function getProductComments(productId, params) {
  return request({
    url: `/merchant/products/${productId}/comments`,
    method: 'get',
    params
  });
}

/**
 * 商家回复评论
 */
export function replyComment(data) {
  return request({
    url: '/merchant/comments/reply',
    method: 'post',
    data
  });
}

/**
 * 删除评论
 */
export function deleteComment(commentId) {
  return request({
    url: `/merchant/comments/${commentId}`,
    method: 'delete'
  });
}

/**
 * 置顶评论
 */
export function topComment(commentId, isTop) {
  return request({
    url: `/merchant/comments/${commentId}/top`,
    method: 'post',
    params: { isTop }
  });
}

/**
 * 获取商品销量数据
 */
export function getProductSalesData(productId) {
  return request({
    url: `/merchant/products/${productId}/sales`,
    method: 'get'
  });
}

/**
 * 分页查询商品列表
 */
export function getProductList(params) {
  return request({
    url: '/merchant/products',
    method: 'get',
    params
  });
}

/**
 * 添加商品
 */
export function addProduct(data) {
  return request({
    url: '/merchant/products',
    method: 'post',
    data
  });
}

/**
 * 更新商品
 */
export function updateProduct(productId, data) {
  return request({
    url: `/merchant/products/${productId}`,
    method: 'put',
    data
  });
}

/**
 * 删除商品
 */
export function deleteProduct(productId) {
  return request({
    url: `/merchant/products/${productId}`,
    method: 'delete'
  });
}

/**
 * 获取分类列表
 */
export function getCategoryList() {
  return request({
    url: '/merchant/categories',
    method: 'get'
  });
}

/**
 * 添加分类
 */
export function addCategory(data) {
  return request({
    url: '/merchant/categories',
    method: 'post',
    data
  });
}

/**
 * 更新分类
 */
export function updateCategory(categoryId, data) {
  return request({
    url: `/merchant/categories/${categoryId}`,
    method: 'put',
    data
  });
}

/**
 * 删除分类
 */
export function deleteCategory(categoryId) {
  return request({
    url: `/merchant/categories/${categoryId}`,
    method: 'delete'
  });
}

/**
 * 上传商品图片
 */
export function uploadGoodsImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/merchant/goods/upload-image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 获取会员详情
 */
export function getMemberDetail(userId) {
  return request({
    url: `/merchant/members/${userId}`,
    method: 'get'
  });
}

/**
 * 更新会员信息
 */
export function updateMemberInfo(userId, data) {
  return request({
    url: `/merchant/members/${userId}`,
    method: 'put',
    data
  });
}

/**
 * 调整会员余额
 */
export function adjustMemberBalance(userId, data) {
  return request({
    url: `/merchant/members/${userId}/balance`,
    method: 'post',
    data
  });
}

/**
 * 重置会员支付密码
 */
export function resetMemberPayPassword(userId, data) {
  return request({
    url: `/merchant/members/${userId}/pay-password`,
    method: 'post',
    data
  });
}

/**
 * 获取会员订单列表
 */
export function getMemberOrders(userId, params) {
  return request({
    url: `/merchant/members/${userId}/orders`,
    method: 'get',
    params
  });
}

/**
 * 标记订单为已完成
 */
export function completeOrder(orderId) {
  return request({
    url: `/merchant/orders/${orderId}/complete`,
    method: 'post'
  });
}

/**
 * 添加物流信息
 */
export function addLogistics(data) {
  return request({
    url: '/logistics/add',
    method: 'post',
    data
  });
}

/**
 * 更新物流信息
 */
export function updateLogistics(logisticsId, data) {
  return request({
    url: `/logistics/update/${logisticsId}`,
    method: 'put',
    data
  });
}

/**
 * 获取订单的物流信息列表
 */
export function getOrderLogistics(orderId) {
  return request({
    url: `/logistics/order/${orderId}`,
    method: 'get'
  });
}

/**
 * 获取物流详情
 */
export function getLogisticsDetail(logisticsId) {
  return request({
    url: `/logistics/detail/${logisticsId}`,
    method: 'get'
  });
}

/**
 * 删除物流信息
 */
export function deleteLogistics(logisticsId) {
  return request({
    url: `/logistics/delete/${logisticsId}`,
    method: 'delete'
  });
}