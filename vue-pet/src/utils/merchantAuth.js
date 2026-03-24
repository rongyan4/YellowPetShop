/**
 * 商家认证工具函数
 * Refresh Token (RT)：HttpOnly Cookie（merchant_token），有效期7天，由后端自动携带
 * Access Token (AT)：localStorage（merchant_accessToken），有效期2分钟，由前端手动携带
 */

const MERCHANT_INFO_KEY = 'merchant_info';
const MERCHANT_AT_KEY = 'merchant_accessToken';

// ====== 商家 Access Token 操作 ======
export const getMerchantAccessToken = () => localStorage.getItem(MERCHANT_AT_KEY);
export const setMerchantAccessToken = (token) => localStorage.setItem(MERCHANT_AT_KEY, token);
export const removeMerchantAccessToken = () => localStorage.removeItem(MERCHANT_AT_KEY);

/**
 * 兼容路由守卫：返回一个可判断登录态的标记值
 * 由于 RT 在 HttpOnly Cookie 中，前端改为从本地商家信息判断
 */
export function getMerchantToken() {
  try {
    const merchantInfo = JSON.parse(localStorage.getItem(MERCHANT_INFO_KEY) || '{}');
    return merchantInfo && merchantInfo.id ? String(merchantInfo.id) : '';
  } catch (error) {
    return '';
  }
}

/**
 * 解析JWT Token（用于读取 payload 信息，token 本身由 Cookie 携带）
 */
export function parseMerchantJWT(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Token解析失败:', error);
    return null;
  }
}
