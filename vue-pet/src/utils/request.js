import axios from 'axios';
import { showToast } from 'vant';

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // API 基础路径
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    // 可以在这里添加 token 等认证信息
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
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
    
    // 如果返回的状态码为 200，说明接口请求成功
    if (res && res.code === 200) {
      return res;
    } else {
      // 如果返回的状态码不是 200，说明有错误
      const errorMsg = res?.msg || '请求失败';
      showToast({
        message: errorMsg,
        type: 'fail'
      });
      return Promise.reject(new Error(errorMsg));
    }
  },
  error => {
    // 对响应错误做点什么
    console.error('响应错误:', error);
    let errorMsg = '网络错误，请稍后重试';
    
    if (error.response) {
      // 服务器返回了错误状态码
      switch (error.response.status) {
        case 400:
          errorMsg = '请求参数错误';
          break;
        case 401:
          errorMsg = '未授权，请重新登录';
          // 可以在这里处理登录跳转
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
    
    showToast({
      message: errorMsg,
      type: 'fail'
    });
    
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
export const post = (url, data = {}) => {
  return service({
    method: 'post',
    url,
    data
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

