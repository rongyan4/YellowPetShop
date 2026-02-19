import { get, post, put, del, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 创建订单
 * @param {Object} orderData 订单数据
 * @returns {Promise} 返回响应对象
 */
export const createOrder = (orderData) => {
  return post('/order/create', orderData);
};

/**
 * 安全创建订单
 * @param {Object} orderData 订单数据
 * @returns {Promise} 成功返回订单详情，失败返回 null
 */
export const createOrderSafe = (orderData) => {
  return safeRequestData(createOrder(orderData));
};

/**
 * 获取订单列表
 * @param {String} status 订单状态（可选）：ALL/PENDING/PAID/SHIPPED/COMPLETED
 * @returns {Promise} 返回响应对象
 */
export const getOrderList = (status) => {
  const params = status ? { status } : {};
  return get('/order/list', params);
};

/**
 * 安全获取订单列表
 * @param {String} status 订单状态（可选）
 * @returns {Promise} 成功返回订单列表，失败返回 null
 */
export const getOrderListSafe = (status) => {
  return safeRequestData(getOrderList(status));
};

/**
 * 获取订单详情
 * @param {Number} orderId 订单ID
 * @returns {Promise} 返回响应对象
 */
export const getOrderDetail = (orderId) => {
  return get(`/order/detail/${orderId}`);
};

/**
 * 安全获取订单详情
 * @param {Number} orderId 订单ID
 * @returns {Promise} 成功返回订单详情，失败返回 null
 */
export const getOrderDetailSafe = (orderId) => {
  return safeRequestData(getOrderDetail(orderId));
};

/**
 * 取消订单
 * @param {Number} orderId 订单ID
 * @returns {Promise} 返回响应对象
 */
export const cancelOrder = (orderId) => {
  return put(`/order/cancel/${orderId}`);
};

/**
 * 安全取消订单
 * @param {Number} orderId 订单ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const cancelOrderSafe = (orderId) => {
  return safeRequestData(cancelOrder(orderId));
};

/**
 * 删除订单
 * @param {Number} orderId 订单ID
 * @returns {Promise} 返回响应对象
 */
export const deleteOrder = (orderId) => {
  return del(`/order/delete/${orderId}`);
};

/**
 * 安全删除订单
 * @param {Number} orderId 订单ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const deleteOrderSafe = (orderId) => {
  return safeRequestData(deleteOrder(orderId));
};

/**
 * 确认收货
 * @param {Number} orderId 订单ID
 * @returns {Promise} 返回响应对象
 */
export const confirmReceipt = (orderId) => {
  return put(`/order/confirm/${orderId}`);
};

/**
 * 确认收货
 * @param {Number} orderId 订单ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const confirmReceiptSafe = (orderId) => {
  return safeRequestData(confirmReceipt(orderId));
};

/**
 * 支付订单
 * @param {Number} orderId 订单ID
 * @param {Object} paymentData 支付数据 { paymentMethod, payPassword }
 * @returns {Promise} 返回响应对象
 */
export const payOrder = (orderId, paymentData) => {
  return post(`/order/pay/${orderId}`, paymentData);
};

/**
 * 安全支付订单
 * @param {Number} orderId 订单ID
 * @param {Object} paymentData 支付数据
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const payOrderSafe = (orderId, paymentData) => {
  return safeRequestData(payOrder(orderId, paymentData));
};
