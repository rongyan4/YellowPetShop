import { get, safeRequest } from '@/utils/request';

/**
 * 获取轮播图数据
 * @returns {Promise} 返回轮播图数据
 */
export const getSwipeImages = () => {
  return get('/home/swipe');
};

/**
 * 安全获取轮播图数据（自动处理错误，无需 try-catch）
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const getSwipeImagesSafe = () => {
  return safeRequest(getSwipeImages());
};

/**
 * 获取推荐商品数据
 * @returns {Promise} 返回推荐商品数据
 */
export const getRecommendGoods = () => {
  return get('/home/recommend');
};

/**
 * 安全获取推荐商品数据（自动处理错误，无需 try-catch）
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const getRecommendGoodsSafe = () => {
  return safeRequest(getRecommendGoods());
};

/**
 * 获取首页数据
 * @returns {Promise} 返回首页数据
 */
export const getHomeData = () => {
  return get('/home');
};

/**
 * 安全获取首页数据（自动处理错误，无需 try-catch）
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const getHomeDataSafe = () => {
  return safeRequest(getHomeData());
};

