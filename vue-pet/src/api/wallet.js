import request from '@/utils/request';

/**
 * 获取用户钱包信息
 */
export function getWalletInfo(userId) {
  return request({
    url: `/wallet/${userId}`,
    method: 'get'
  });
}

/**
 * 获取当前用户钱包信息
 */
export function getCurrentWalletInfo() {
  return request({
    url: '/wallet/info',
    method: 'get'
  });
}

/**
 * 设置支付密码
 */
export function setPayPassword(data) {
  return request({
    url: '/wallet/set-pay-password',
    method: 'post',
    data
  });
}

/**
 * 修改支付密码
 */
export function updatePayPassword(data) {
  return request({
    url: '/wallet/update-pay-password',
    method: 'post',
    data
  });
}

/**
 * 验证支付密码
 */
export function verifyPayPassword(password) {
  return request({
    url: '/wallet/verify-pay-password',
    method: 'post',
    data: { password }
  });
}

/**
 * 获取钱包交易记录
 */
export function getWalletTransactions(params) {
  return request({
    url: '/wallet/transactions',
    method: 'get',
    params
  });
}

/**
 * 充值
 */
export function recharge(data) {
  return request({
    url: '/wallet/recharge',
    method: 'post',
    data
  });
}

/**
 * 提现
 */
export function withdraw(data) {
  return request({
    url: '/wallet/withdraw',
    method: 'post',
    data
  });
}

/**
 * 查询是否已设置支付密码
 */
export function hasPayPassword() {
  return request({
    url: '/wallet/has-pay-password',
    method: 'get'
  });
}