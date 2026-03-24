/**
 * 认证工具函数
 * Refresh Token (RT)：HttpOnly Cookie，有效期7天，由后端自动携带，无需前端操作
 * Access Token (AT)：localStorage，有效期2分钟，由前端手动携带至 Authorization header
 */

const USER_INFO_KEY = 'userInfo';
const ACCESS_TOKEN_KEY = 'accessToken';

// ====== Access Token 操作 ======
export const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_KEY);
export const setAccessToken = (token) => localStorage.setItem(ACCESS_TOKEN_KEY, token);
export const removeAccessToken = () => localStorage.removeItem(ACCESS_TOKEN_KEY);

// ====== 用户信息操作 ======
export const getUserInfo = () => {
  const userInfo = localStorage.getItem(USER_INFO_KEY);
  return userInfo ? JSON.parse(userInfo) : null;
};

export const setUserInfo = (userInfo) => {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
};

export const removeUserInfo = () => {
  localStorage.removeItem(USER_INFO_KEY);
};

/**
 * 清除所有认证信息（AT + 用户信息）
 * RT（Cookie）由后端通过 logout 接口清除
 */
export const clearAuth = () => {
  removeAccessToken();
  removeUserInfo();
};

/**
 * 检查是否已登录（根据本地存储的用户信息判断）
 */
export const isAuthenticated = () => {
  return !!getUserInfo();
};

/**
 * 解析JWT token（不验证签名，仅解析payload）
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
