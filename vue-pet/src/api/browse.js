import { get, post, del, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 添加浏览记录
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 返回响应对象
 */
export const addBrowseHistory = (commodityId) => {
  return post(`/browse/add?commodityId=${commodityId}`);
};

/**
 * 安全添加浏览记录
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const addBrowseHistorySafe = (commodityId) => {
  return safeRequestData(addBrowseHistory(commodityId));
};

/**
 * 获取浏览记录列表
 * @param {Number} limit 限制数量，默认100
 * @returns {Promise} 返回响应对象
 */
export const getBrowseHistoryList = (limit = 100) => {
  return get('/browse/list', { limit });
};

/**
 * 安全获取浏览记录列表
 * @param {Number} limit 限制数量，默认100
 * @returns {Promise} 成功返回浏览记录列表，失败返回 null
 */
export const getBrowseHistoryListSafe = (limit = 100) => {
  return safeRequestData(getBrowseHistoryList(limit));
};

/**
 * 清空浏览记录
 * @returns {Promise} 返回响应对象
 */
export const clearBrowseHistory = () => {
  return del('/browse/clear');
};

/**
 * 安全清空浏览记录
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const clearBrowseHistorySafe = () => {
  return safeRequestData(clearBrowseHistory());
};
