import { createStore } from "vuex";

/**
 * Vuex Store - Vue3 风格
 * 用户状态管理
 */
export default createStore({
  state: () => ({
    // 用户 token
    token: localStorage.getItem('token') || '',
    // 用户信息
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
  }),
  
  getters: {
    // 获取 token
    getToken: (state) => state.token,
    // 获取用户信息
    getUserInfo: (state) => state.userInfo,
    // 判断是否已登录
    isLoggedIn: (state) => !!state.token,
  },
  
  mutations: {
    // 设置 token
    SET_TOKEN(state, token) {
      state.token = token;
      if (token) {
        localStorage.setItem('token', token);
      } else {
        localStorage.removeItem('token');
      }
    },
    
    // 设置用户信息
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo;
      if (userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
      } else {
        localStorage.removeItem('userInfo');
      }
    },
    
    // 清除登录信息
    CLEAR_LOGIN_INFO(state) {
      state.token = '';
      state.userInfo = null;
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    },
  },
  
  actions: {
    // 登录成功后保存 token 和用户信息
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token);
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
