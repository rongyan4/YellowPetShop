import { defineStore } from 'pinia';

/**
 * 商家状态管理 Store - Pinia
 * merchant_token 已改为 HttpOnly Cookie 存储，Store 只维护商家信息
 */
export const useMerchantStore = defineStore('merchant', {
  state: () => ({
    merchantInfo: JSON.parse(localStorage.getItem('merchant_info') || '{}')
  }),

  getters: {
    isLoggedIn: (state) => !!state.merchantInfo && !!state.merchantInfo.id,
    getMerchantInfo: (state) => state.merchantInfo
  },

  actions: {
    // 商家登录（token 由后端写入 HttpOnly Cookie）
    login(data) {
      this.merchantInfo = data.merchantInfo || {};
      localStorage.setItem('merchant_info', JSON.stringify(this.merchantInfo));
    },

    // 更新商家信息
    updateMerchantInfo(info) {
      this.merchantInfo = info;
      localStorage.setItem('merchant_info', JSON.stringify(info));
    },

    // 商家登出（token Cookie 由后端 logout 接口清除）
    logout() {
      this.merchantInfo = {};
      localStorage.removeItem('merchant_info');
    }
  }
});
