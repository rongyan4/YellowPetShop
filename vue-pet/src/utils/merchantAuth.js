/**
 * 获取商家Token
 */
export function getMerchantToken() {
  return localStorage.getItem('merchant_token');
}

/**
 * 设置商家Token
 */
export function setMerchantToken(token) {
  localStorage.setItem('merchant_token', token);
}

/**
 * 移除商家Token
 */
export function removeMerchantToken() {
  localStorage.removeItem('merchant_token');
}

/**
 * 解析JWT Token
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
