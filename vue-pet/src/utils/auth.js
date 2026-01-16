/**
 * 认证工具函数
 * 用于处理JWT token和用户认证相关操作
 */

const TOKEN_KEY = 'token';
const USER_INFO_KEY = 'userInfo';

/**
 * 获取token
 * @returns {string|null}
 */
export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

/**
 * 设置token
 * @param {string} token
 */
export const setToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token);
};

/**
 * 移除token
 */
export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};

/**
 * 获取用户信息
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
 * 清除所有认证信息
 */
export const clearAuth = () => {
  removeToken();
  removeUserInfo();
};

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export const isAuthenticated = () => {
  return !!getToken();
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

/**
 * 检查token是否过期
 * @param {string} token
 * @returns {boolean}
 */
export const isTokenExpired = (token) => {
  const payload = parseJWT(token);
  if (!payload || !payload.exp) {
    return true;
  }
  // exp是秒级时间戳，需要转换为毫秒
  return Date.now() >= payload.exp * 1000;
};

/**
 * 获取token剩余有效时间（秒）
 * @param {string} token
 * @returns {number}
 */
export const getTokenRemainingTime = (token) => {
  const payload = parseJWT(token);
  if (!payload || !payload.exp) {
    return 0;
  }
  const remaining = payload.exp * 1000 - Date.now();
  return Math.max(0, Math.floor(remaining / 1000));
};
