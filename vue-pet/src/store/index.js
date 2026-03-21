import { createStore } from "vuex";

/**
 * Vuex Store - Vue3 风格
 * token 已改为 HttpOnly Cookie 存储，Store 只维护用户信息
 */
export default createStore({
  state: () => ({
    // 用户信息
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
  }),
  
  getters: {
    // 获取用户信息
    getUserInfo: (state) => state.userInfo,
    // 判断是否已登录
    isLoggedIn: (state) => !!state.userInfo,
  },
  
  mutations: {
    // 设置用户信息
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo;
      if (userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
      } else {
        localStorage.removeItem('userInfo');
      }
    },
    
    // 清除登录信息（token Cookie 由后端清除）
    CLEAR_LOGIN_INFO(state) {
      state.userInfo = null;
      localStorage.removeItem('userInfo');
    },
  },
  
  actions: {
    // 登录成功后保存用户信息
    login({ commit }, { userInfo }) {
      commit('SET_USER_INFO', userInfo);
    },
    
    // 退出登录
    logout({ commit }) {
      commit('CLEAR_LOGIN_INFO');
    },
    
    // 更新用户信息
    updateUserInfo({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo);
    },
  },
  
  modules: {},
});
