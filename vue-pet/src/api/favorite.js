import { get, post, del, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 添加收藏
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 返回响应对象
 */
export const addFavorite = (commodityId) => {
  return post(`/favorite/add?commodityId=${commodityId}`);
};

/**
 * 安全添加收藏
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const addFavoriteSafe = (commodityId) => {
  return safeRequestData(addFavorite(commodityId));
};

/**
 * 取消收藏
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 返回响应对象
 */
export const removeFavorite = (commodityId) => {
  return del('/favorite/remove', { commodityId });
};

/**
 * 安全取消收藏
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const removeFavoriteSafe = (commodityId) => {
  return safeRequestData(removeFavorite(commodityId));
};

/**
 * 检查是否已收藏
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 返回响应对象
 */
export const checkFavorite = (commodityId) => {
  return get('/favorite/check', { commodityId });
};

/**
 * 安全检查是否已收藏
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 成功返回布尔值，失败返回 null
 */
export const checkFavoriteSafe = (commodityId) => {
  return safeRequestData(checkFavorite(commodityId));
};

/**
 * 获取收藏列表
 * @returns {Promise} 返回响应对象
 */
export const getFavoriteList = () => {
  return get('/favorite/list');
};

/**
 * 安全获取收藏列表
 * @returns {Promise} 成功返回收藏列表，失败返回 null
 */
export const getFavoriteListSafe = () => {
  return safeRequestData(getFavoriteList());
};
