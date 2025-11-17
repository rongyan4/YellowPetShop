import { get, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 获取轮播图数据
 * @returns {Promise} 返回轮播图数据响应对象
 */
export const getSwipeImages = () => {
  return get('/home/swipe');
};

/**
 * 安全获取轮播图数据（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @returns {Promise} 成功返回数据数组，失败返回 null
 */
export const getSwipeImagesSafe = () => {
  return safeRequestData(getSwipeImages());
};

/**
 * 获取推荐商品数据
 * @returns {Promise} 返回推荐商品数据响应对象
 */
export const getRecommendGoods = () => {
  return get('/home/recommend');
};

/**
 * 安全获取推荐商品数据（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @returns {Promise} 成功返回商品数组，失败返回 null
 */
export const getRecommendGoodsSafe = () => {
  return safeRequestData(getRecommendGoods());
};

/**
 * 获取首页数据
 * @returns {Promise} 返回首页数据响应对象
 */
export const getHomeData = () => {
  return get('/home');
};

/**
 * 安全获取首页数据（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const getHomeDataSafe = () => {
  return safeRequestData(getHomeData());
};

