import axios from 'axios';
import { showToast } from 'vant';

// 全局请求计数器
let globalRequestCounter = 0;

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api/', // API 基础路径
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'Cache-Control': 'no-cache',
    'Pragma': 'no-cache'
  }
});

// 请求拦截器 - JWT Token 处理
service.interceptors.request.use(
  config => {
    const requestId = ++globalRequestCounter;
    config.headers['X-Request-ID'] = requestId;
    
    console.log('=== 发送请求 [#' + requestId + '] ===');
    console.log('URL:', config.url);
    console.log('Method:', config.method);
    console.log('Data:', config.data);
    console.log('Timestamp:', new Date().toISOString());
    
    // 判断是否是商家端请求
    const isMerchantRequest = config.url.includes('/merchant');
    
    // 根据请求类型选择对应的 token
    let token;
    if (isMerchantRequest) {
      token = localStorage.getItem('merchant_token');
    } else {
      token = localStorage.getItem('token');
    }
    
    // 如果 token 存在，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // 添加时间戳防止缓存
    config.headers['X-Request-Time'] = Date.now();
    
    // 禁用缓存
    if (config.method === 'post' || config.method === 'put') {
      config.headers['Cache-Control'] = 'no-cache';
      config.headers['Pragma'] = 'no-cache';
    }
    
    // 可以在这里添加其他请求头信息
    return config;
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;
    const requestId = response.config.headers['X-Request-ID'];
    
    console.log('=== 收到响应 [#' + requestId + '] ===');
    console.log('URL:', response.config.url);
    console.log('Status:', response.status);
    console.log('Response:', res);
    console.log('Timestamp:', new Date().toISOString());
    
    // 如果后端返回的 code 不是 200，视为业务错误
    if (res.code && res.code !== 200) {
      console.error('业务错误 [#' + requestId + ']:', res);
      console.error('错误时间:', new Date().toISOString());
      // 抛出错误，让 catch 块捕获
      const error = new Error(res.msg || '请求失败');
      error.response = { data: res };
      return Promise.reject(error);
    }
    
    // 直接返回响应数据，让业务代码自己处理不同的 code
    // 这样可以根据具体业务需求决定是否显示错误提示
    return res;
  },
  error => {
    // 对响应错误做点什么
    console.error('响应错误:', error);
    let errorMsg = '网络错误，请稍后重试';
    let shouldShowToast = true;
    
    if (error.response) {
      // 服务器返回了错误状态码
      switch (error.response.status) {
        case 400:
          errorMsg = '请求参数错误';
          break;
        case 401:
          // 判断是否是商家端请求
          const isMerchantRequest = error.config.url.includes('/merchant');
          
          if (isMerchantRequest) {
            // 清除商家端 token
            localStorage.removeItem('merchant_token');
            localStorage.removeItem('merchant_info');
            // 跳转到商家登录页
            window.location.href = '/merchant/login';
          } else {
            // 清除客户端 token 和用户信息
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
          }
          // 不显示"登录已过期"的提示，让各个页面自己处理登录逻辑
          shouldShowToast = false;
          break;
        case 403:
          errorMsg = '拒绝访问';
          break;
        case 404:
          errorMsg = '请求的资源不存在';
          break;
        case 500:
          errorMsg = '服务器内部错误';
          break;
        default:
          errorMsg = `请求失败: ${error.response.status}`;
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      errorMsg = '网络连接失败，请检查网络';
    }
    
    // 只在需要时显示提示
    if (shouldShowToast) {
      showToast({
        message: errorMsg,
        type: 'fail'
      });
    }
    
    return Promise.reject(error);
  }
);

// 封装 GET 请求
export const get = (url, params = {}) => {
  return service({
    method: 'get',
    url,
    params
  });
};

// 封装 POST 请求
export const post = (url, data = {}, config = {}) => {
  return service({
    method: 'post',
    url,
    data,
    ...config
  });
};

// 封装 PUT 请求
export const put = (url, data = {}) => {
  return service({
    method: 'put',
    url,
    data
  });
};

// 封装 DELETE 请求
export const del = (url, params = {}) => {
  return service({
    method: 'delete',
    url,
    params
  });
};

/**
 * 安全请求包装函数，自动处理错误，无需 try-catch
 * @param {Promise} promise - 请求 Promise
 * @param {*} defaultValue - 错误时的默认返回值，默认为 null
 * @returns {Promise} 返回处理后的 Promise，成功返回响应对象，失败返回默认值
 * 
 * @example
 * // 使用方式
 * const result = await safeRequest(getSwipeImages());
 * if (result) {
 *   SwipeImages.value = result.data;
 * }
 */
export const safeRequest = async (promise, defaultValue = null) => {
  try {
    const result = await promise;
    return result;
  } catch (error) {
    // 错误已经在拦截器中处理并提示了，这里只返回默认值
    return defaultValue;
  }
};

/**
 * 安全请求并提取数据
 * 自动处理错误并提取响应数据，无需手动提取 data 字段
 * @param {Promise} promise - 请求 Promise
 * @param {*} defaultValue - 错误时的默认返回值，默认为 null
 * @returns {Promise} 返回处理后的 Promise，成功返回提取的数据，失败返回默认值
 * 
 * @example
 * // 使用方式
 * const data = await safeRequestData(getSwipeImages());
 * if (data) {
 *   SwipeImages.value = data;
 * }
 */
export const safeRequestData = async (promise, defaultValue = null) => {
  try {
    const result = await promise;
    // 提取响应数据
    if (result && result.data !== undefined) {
      return result.data;
    }
    return result;
  } catch (error) {
    // 错误已经在拦截器中处理并提示了，这里只返回默认值
    return defaultValue;
  }
};

// 导出 axios 实例，以便需要时直接使用
export default service;

