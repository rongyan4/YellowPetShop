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
    if (res.code === 200) {
      return res;
    } else {
      // 如果返回的状态码不是 200，说明有错误
      const errorMsg = res.msg || '请求失败';
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

// 导出 axios 实例，以便需要时直接使用
export default service;

