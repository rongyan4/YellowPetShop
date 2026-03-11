import request from '@/utils/request';

/**
 * 获取会员列表
 */
export function getMerchantMemberList(params) {
  return request({
    url: '/merchant/members/list',
    method: 'get',
    params
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
 * 获取会员地址列表
 */
export function getMemberAddressList(userId) {
  return request({
    url: '/address/list',
    method: 'get',
    params: { userId }
  });
}

/**
 * 添加会员地址
 */
export function addMemberAddress(userId, data) {
  return request({
    url: '/address/add',
    method: 'post',
    data: {
      ...data,
      userId
    }
  });
}

/**
 * 更新会员地址
 */
export function updateMemberAddress(addressId, data) {
  return request({
    url: `/address/update/${addressId}`,
    method: 'put',
    data
  });
}

/**
 * 删除会员地址
 */
export function deleteMemberAddress(addressId) {
  return request({
    url: `/address/delete/${addressId}`,
    method: 'delete'
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
    url: `/merchant/members/${userId}/reset-pay-password`,
    method: 'post',
    data
  });
}

/**
 * 更新会员状态
 */
export function updateMemberStatus(params) {
  return request({
    url: '/merchant/members/status',
    method: 'put',
    params
  });
}

/**
 * 删除会员
 */
export function deleteMember(id) {
  return request({
    url: `/merchant/members/delete/${id}`,
    method: 'delete'
  });
}
