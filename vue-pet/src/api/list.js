import { get, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 获取分类列表数据
 * @returns {Promise} 返回分类列表数据响应对象
 */
export const getCategoryList = () => {
  return get('/list/categories');
};

/**
 * 安全获取分类列表数据（自动处理错误，无需 try-catch）
 * 直接返回数据，无需手动提取 data 字段
 * @returns {Promise} 成功返回分类数组，失败返回 null
 */
export const getCategoryListSafe = () => {
  return safeRequestData(getCategoryList());
};

/**
 * 根据分类拼音简写获取商品列表
 * @param {string} pinyin 分类拼音简写
 * @returns {Promise} 返回商品列表数据响应对象
 */
export const getGoodsByCategory = (pinyin) => {
  return get('/list/goods', { pinyin });
};

/**
 * 安全获取分类商品数据（自动处理错误，无需 try-catch）
 * @param {string} pinyin 分类拼音简写
 * @returns {Promise} 成功返回商品数组，失败返回 null
 */
export const getGoodsByCategorySafe = (pinyin) => {
  return safeRequestData(getGoodsByCategory(pinyin));
};

