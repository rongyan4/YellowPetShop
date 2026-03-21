import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * 用户状态管理 Store - Pinia
 * token 已改为 HttpOnly Cookie 存储，Store 只维护用户信息
 */
export const useUserStore = defineStore('user', () => {
  // State
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'));

  // Getters
  const getUserInfo = computed(() => userInfo.value);
  const isLoggedIn = computed(() => !!userInfo.value);

  /**
   * 设置用户信息
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
   * 登录成功后保存用户信息（token 由后端写入 HttpOnly Cookie）
   */
  function login({ userInfo: newUserInfo }) {
    setUserInfo(newUserInfo);
  }

  /**
   * 退出登录，清除用户信息（token Cookie 由后端 logout 接口清除）
   */
  function logout() {
    userInfo.value = null;
    localStorage.removeItem('userInfo');
  }

  /**
   * 更新用户信息
   */
  function updateUserInfo(newUserInfo) {
    setUserInfo(newUserInfo);
  }

  return {
    userInfo,
    getUserInfo,
    isLoggedIn,
    setUserInfo,
    login,
    logout,
    updateUserInfo,
  };
});
