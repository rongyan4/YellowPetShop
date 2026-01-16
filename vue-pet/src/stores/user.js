import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * 用户状态管理 Store - Pinia
 * 使用 Composition API 风格
 */
export const useUserStore = defineStore('user', () => {
  // State - 使用 ref 定义响应式状态
  const token = ref(localStorage.getItem('token') || '');
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'));

  // Getters - 使用 computed 定义计算属性
  const getToken = computed(() => token.value);
  const getUserInfo = computed(() => userInfo.value);
  const isLoggedIn = computed(() => !!token.value);

  // Actions - 定义方法
  /**
   * 设置 token
   * @param {string} newToken - 新的 token
   */
  function setToken(newToken) {
    token.value = newToken;
    if (newToken) {
      localStorage.setItem('token', newToken);
    } else {
      localStorage.removeItem('token');
    }
  }

  /**
   * 设置用户信息
   * @param {Object} newUserInfo - 新的用户信息
   */
  function setUserInfo(newUserInfo) {
    userInfo.value = newUserInfo;
    if (newUserInfo) {
      localStorage.setItem('userInfo', JSON.stringify(newUserInfo));
    } else {
      localStorage.removeItem('userInfo');
    }
  }

  /**
   * 登录成功后保存 token 和用户信息
   * @param {Object} loginData - 登录数据
   * @param {string} loginData.token - JWT token
   * @param {Object} loginData.userInfo - 用户信息
   */
  function login({ token: newToken, userInfo: newUserInfo }) {
    setToken(newToken);
    setUserInfo(newUserInfo);
  }

  /**
   * 退出登录，清除所有登录信息
   */
  function logout() {
    token.value = '';
    userInfo.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
  }

  /**
   * 更新用户信息
   * @param {Object} newUserInfo - 新的用户信息
   */
  function updateUserInfo(newUserInfo) {
    setUserInfo(newUserInfo);
  }

  // 返回所有需要暴露的状态、计算属性和方法
  return {
    // State
    token,
    userInfo,
    // Getters
    getToken,
    getUserInfo,
    isLoggedIn,
    // Actions
    setToken,
    setUserInfo,
    login,
    logout,
    updateUserInfo,
  };
});
