/**
 * 认证工具函数
 * token 已改为 HttpOnly Cookie 存储，前端不再直接读写 token
 */

const USER_INFO_KEY = 'userInfo';

/**
 * 获取用户信息（从 localStorage）
 * @returns {Object|null}
 */
export const getUserInfo = () => {
  const userInfo = localStorage.getItem(USER_INFO_KEY);
  return userInfo ? JSON.parse(userInfo) : null;
};

/**
 * 设置用户信息
 * @param {Object} userInfo
 */
export const setUserInfo = (userInfo) => {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
};

/**
 * 移除用户信息
 */
export const removeUserInfo = () => {
  localStorage.removeItem(USER_INFO_KEY);
};

/**
 * 清除所有认证信息（不含 token，token 由后端通过 Cookie 清除）
 */
export const clearAuth = () => {
  removeUserInfo();
};

/**
 * 检查是否已登录（根据本地存储的用户信息判断）
 * @returns {boolean}
 */
export const isAuthenticated = () => {
  return !!getUserInfo();
};

/**
 * 解析JWT token（不验证签名，仅解析payload）
 * @param {string} token
 * @returns {Object|null}
 */
export const parseJWT = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('解析JWT失败:', error);
    return null;
  }
};
