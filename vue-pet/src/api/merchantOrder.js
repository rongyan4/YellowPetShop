import request from '@/utils/request';

/**
 * 获取订单列表
 */
export function getMerchantOrderList(params) {
  return request({
    url: '/merchant/orders',
    method: 'get',
    params
  });
}

/**
 * 获取订单详情
 */
export function getMerchantOrderDetail(id) {
  return request({
    url: `/merchant/orders/${id}`,
    method: 'get'
  });
}

/**
 * 修改订单价格
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
 * 更新物流状态（已废弃，使用 shipOrder 代替）
 * @deprecated
 */
export function updateShippingStatus(params) {
  return request({
    url: '/merchant/orders/ship',
    method: 'post',
    data: {
      orderId: params.orderId,
      trackingNo: params.trackingNo,
      shippingCompany: params.shippingStatus
    }
  });
}

/**
 * 取消订单（暂未实现）
 * @deprecated
 */
export function cancelOrder(params) {
  return request({
    url: '/merchant/order/cancel',
    method: 'put',
    params
  });
}
