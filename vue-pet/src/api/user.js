import request from '@/utils/request';

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  });
};

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.email - 邮箱
 * @param {string} data.nickname - 昵称（可选）
 * @returns {Promise}
 */
export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  });
};

/**
 * 获取用户信息
 * @param {number} id - 用户ID
 * @returns {Promise}
 */
export const getUserInfo = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  });
};

/**
 * 获取当前登录用户信息
 * @returns {Promise}
 */
export const getCurrentUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  });
};

/**
 * 用户退出登录
 * @returns {Promise}
 */
export const logout = () => {
  return request({
    url: '/user/logout',
    method: 'post'
  });
};