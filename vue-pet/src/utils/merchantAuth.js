/**
 * 商家认证工具函数
 * merchant_token 已改为 HttpOnly Cookie 存储，前端不再直接读写 token
 */

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
