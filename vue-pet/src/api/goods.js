import { get, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 获取所有商品列表
 * @returns {Promise} 返回商品列表响应对象
 */
export const getAllGoods = () => {
  return get('/goods/all');
};

/**
 * 安全获取所有商品列表（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @returns {Promise} 成功返回商品数组，失败返回 null
 */
export const getAllGoodsSafe = () => {
  return safeRequestData(getAllGoods());
};

/**
 * 分页获取商品列表
 * @param {Number} current 当前页码（从1开始）
 * @param {Number} size 每页大小，默认8条
 * @returns {Promise} 返回分页结果响应对象
 */
export const getGoodsByPage = (current = 1, size = 8) => {
  return get('/goods/page', { current, size });
};

/**
 * 安全分页获取商品列表（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @param {Number} current 当前页码（从1开始）
 * @param {Number} size 每页大小，默认8条
 * @returns {Promise} 成功返回分页结果对象，失败返回 null
 */
export const getGoodsByPageSafe = (current = 1, size = 8) => {
  return safeRequestData(getGoodsByPage(current, size));
};

/**
 * 获取商品详情
 * @param {Number} id 商品ID
 * @returns {Promise} 返回商品详情响应对象
 */
export const getGoodDetail = (id) => {
  return get('/goods/detail', { id });
};

/**
 * 安全获取商品详情（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @param {Number} id 商品ID
 * @returns {Promise} 成功返回商品详情，失败返回 null
 */
export const getGoodDetailSafe = (id) => {
  return safeRequestData(getGoodDetail(id));
};

/**
 * 搜索商品
 * @param {String} keyword 搜索关键词
 * @returns {Promise} 返回搜索结果响应对象
 */
export const searchGoods = (keyword) => {
  return get('search', { keyword });
};

/**
 * 安全搜索商品（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @param {String} keyword 搜索关键词
 * @returns {Promise} 成功返回搜索结果数组，失败返回 null
 */
export const searchGoodsSafe = (keyword) => {
  return safeRequestData(searchGoods(keyword));
};

/**
 * 获取所有分类列表
 * @returns {Promise} 返回分类列表响应对象
 */
export const getAllCategories = () => {
  return get('/category/all');
};

/**
 * 安全获取所有分类列表
 * @returns {Promise} 成功返回分类数组，失败返回 null
 */
export const getAllCategoriesSafe = () => {
  return safeRequestData(getAllCategories());
};

/**
 * 获取分类及其商品预览
 * @returns {Promise} 返回分类及商品预览响应对象
 */
export const getCategoriesWithPreview = () => {
  return get('/category/preview');
};

/**
 * 安全获取分类及其商品预览
 * @returns {Promise} 成功返回分类及商品预览，失败返回 null
 */
export const getCategoriesWithPreviewSafe = () => {
  return safeRequestData(getCategoriesWithPreview());
};

/**
 * 按分类分页获取商品列表
 * @param {Number} categoryId 分类ID
 * @param {Number} current 当前页码（从1开始）
 * @param {Number} size 每页大小，默认10条
 * @returns {Promise} 返回分页结果响应对象
 */
export const getGoodsByCategory = (categoryId, current = 1, size = 10) => {
  return get('/category/goods', { categoryId, current, size });
};

/**
 * 安全按分类分页获取商品列表
 * @param {Number} categoryId 分类ID
 * @param {Number} current 当前页码（从1开始）
 * @param {Number} size 每页大小，默认10条
 * @returns {Promise} 成功返回分页结果对象，失败返回 null
 */
export const getGoodsByCategorySafe = (categoryId, current = 1, size = 10) => {
  return safeRequestData(getGoodsByCategory(categoryId, current, size));
};
