import { get } from '@/utils/request';

/**
 * 获取轮播图数据
 * @returns {Promise} 返回轮播图数据
 */
export const getSwipeImages = () => {
  return get('/home/swipe');
};

/**
 * 获取推荐商品数据
 * @returns {Promise} 返回推荐商品数据
 */
export const getRecommendGoods = () => {
  return get('/home/recommend');
};

/**
 * 获取首页数据
 * @returns {Promise} 返回首页数据
 */
export const getHomeData = () => {
  return get('/home');
};

