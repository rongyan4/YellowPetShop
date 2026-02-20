import { defineStore } from 'pinia';

export const useMerchantStore = defineStore('merchant', {
  state: () => ({
    token: localStorage.getItem('merchant_token') || '',
    merchantInfo: JSON.parse(localStorage.getItem('merchant_info') || '{}')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    getMerchantInfo: (state) => state.merchantInfo
  },

  actions: {
    // 商家登录
    login(data) {
      this.token = data.token;
      this.merchantInfo = data.merchantInfo || {};
      localStorage.setItem('merchant_token', data.token);
      localStorage.setItem('merchant_info', JSON.stringify(this.merchantInfo));
    },

    // 更新商家信息
    updateMerchantInfo(info) {
      this.merchantInfo = info;
      localStorage.setItem('merchant_info', JSON.stringify(info));
    },

    // 商家登出
    logout() {
      this.token = '';
      this.merchantInfo = {};
      localStorage.removeItem('merchant_token');
      localStorage.removeItem('merchant_info');
    }
  }
});
