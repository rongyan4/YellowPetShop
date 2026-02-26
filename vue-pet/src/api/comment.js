import { get, post, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 分页获取商品评论列表
 * @param {Number} commodityId 商品ID
 * @param {Number} current 当前页码（从1开始）
 * @param {Number} size 每页大小，默认10条
 * @returns {Promise} 返回分页结果响应对象
 */
export const getCommentsByPage = (commodityId, current = 1, size = 10) => {
  return get('/comments/page', { commodityId, current, size });
};

/**
 * 安全分页获取商品评论列表
 * @param {Number} commodityId 商品ID
 * @param {Number} current 当前页码（从1开始）
 * @param {Number} size 每页大小，默认10条
 * @returns {Promise} 成功返回分页结果对象，失败返回 null
 */
export const getCommentsByPageSafe = (commodityId, current = 1, size = 10) => {
  return safeRequestData(getCommentsByPage(commodityId, current, size));
};

/**
 * 获取商品评论总数
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 返回评论总数响应对象
 */
export const getCommentCount = (commodityId) => {
  return get('/comments/count', { commodityId });
};

/**
 * 安全获取商品评论总数
 * @param {Number} commodityId 商品ID
 * @returns {Promise} 成功返回评论总数，失败返回 null
 */
export const getCommentCountSafe = (commodityId) => {
  return safeRequestData(getCommentCount(commodityId));
};

/**
 * 创建评论
 * @param {Object} data 评论数据
 * @returns {Promise} 返回创建结果响应对象
 */
export const createComment = (data) => {
  return post('/comments/create', data);
};

/**
 * 安全创建评论
 * @param {Object} data 评论数据
 * @returns {Promise} 成功返回结果，失败返回 null
 */
export const createCommentSafe = (data) => {
  return safeRequestData(createComment(data));
};

/**
 * 上传评论图片
 * @param {FormData} formData 包含图片文件的表单数据
 * @returns {Promise} 返回上传结果响应对象
 */
export const uploadCommentImage = (formData) => {
  return post('/comments/upload_image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 安全上传评论图片
 * @param {FormData} formData 包含图片文件的表单数据
 * @returns {Promise} 成功返回图片URL，失败返回 null
 */
export const uploadCommentImageSafe = (formData) => {
  return safeRequestData(uploadCommentImage(formData));
};

/**
 * 获取订单商品的评论状态
 * @param {Number} orderId 订单ID
 * @returns {Promise} 返回评论状态列表
 */
export const getOrderCommentStatus = (orderId) => {
  return get('/comments/order_status', { orderId });
};

/**
 * 安全获取订单商品的评论状态
 * @param {Number} orderId 订单ID
 * @returns {Promise} 成功返回评论状态列表，失败返回 null
 */
export const getOrderCommentStatusSafe = (orderId) => {
  return safeRequestData(getOrderCommentStatus(orderId));
};
