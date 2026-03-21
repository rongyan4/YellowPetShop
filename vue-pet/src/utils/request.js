import axios from 'axios';
import { showToast } from 'vant';

// 全局请求计数器
let globalRequestCounter = 0;

// 图片基础URL配置（根据环境变量）
export const IMAGE_BASE_URL = process.env.VUE_APP_IMAGE_BASE_URL || 'http://localhost:3000';

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api/', // API 基础路径
  timeout: 10000, // 请求超时时间
  withCredentials: true, // 携带 HttpOnly Cookie
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'Cache-Control': 'no-cache',
    'Pragma': 'no-cache'
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    const requestId = ++globalRequestCounter;
    config.headers['X-Request-ID'] = requestId;
    
    console.log('=== 发送请求 [#' + requestId + '] ===');
    console.log('URL:', config.url);
    console.log('Method:', config.method);
    console.log('Data:', config.data);
    console.log('Timestamp:', new Date().toISOString());
    
    // 添加时间戳防止缓存
    config.headers['X-Request-Time'] = Date.now();
    
    // 禁用缓存
    if (config.method === 'post' || config.method === 'put') {
      config.headers['Cache-Control'] = 'no-cache';
      config.headers['Pragma'] = 'no-cache';
    }
    
    return config;
  },
  error => {
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
      const error = new Error(res.msg || '请求失败');
      error.response = { data: res };
      return Promise.reject(error);
    }
    
    return res;
  },
  error => {
    console.error('响应错误:', error);
    let errorMsg = '网络错误，请稍后重试';
    let shouldShowToast = true;
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          errorMsg = '请求参数错误';
          break;
        case 401: {
          // 判断是否是商家端请求
          const isMerchantRequest = error.config.url.includes('/merchant');
          if (isMerchantRequest) {
            window.location.href = '/merchant/login';
          }
          // token 存在 HttpOnly Cookie 中，无需手动清除
          shouldShowToast = false;
          break;
        }
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
      errorMsg = '网络连接失败，请检查网络';
    }
    
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
 */
export const safeRequest = async (promise, defaultValue = null) => {
  try {
    const result = await promise;
    return result;
  } catch (error) {
    return defaultValue;
  }
};

/**
 * 安全请求并提取数据
 */
export const safeRequestData = async (promise, defaultValue = null) => {
  try {
    const result = await promise;
    if (result && result.data !== undefined) {
      return result.data;
    }
    return result;
  } catch (error) {
    if (error.response && error.response.data) {
      throw error;
    }
    return defaultValue;
  }
};

/**
 * 获取完整的图片URL
 */
export const getImageUrl = (relativePath) => {
  if (!relativePath) return '';
  if (relativePath.startsWith('http://') || relativePath.startsWith('https://')) {
    return relativePath;
  }
  return IMAGE_BASE_URL + relativePath;
};

// 导出 axios 实例
export default service;
