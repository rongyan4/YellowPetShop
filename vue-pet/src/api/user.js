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

/**
 * 更新用户信息（昵称、性别、生日）
 * @param {Object} data - 用户信息
 * @param {string} data.username - 用户名
 * @param {string} data.nickname - 昵称
 * @param {string} data.gender - 性别
 * @param {string} data.birthday - 生日 (YYYY-MM-DD)
 * @returns {Promise}
 */
export const updateUserInfo = (data) => {
  return request({
    url: '/user/update_info',
    method: 'post',
    data
  });
};

/**
 * 上传头像
 * @param {FormData} formData - 包含头像文件的表单数据
 * @returns {Promise}
 */
export const uploadAvatar = (formData) => {
  return request({
    url: '/user/upload_avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};
