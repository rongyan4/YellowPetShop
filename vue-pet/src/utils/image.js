/**
 * 图片工具类
 * 统一处理图片URL的拼接
 */

// 图片基础URL（根据环境变量配置）
const IMAGE_BASE_URL = process.env.VUE_APP_IMAGE_BASE_URL || 'http://localhost:3000';

/**
 * 获取完整的图片URL
 * @param {string} relativePath - 相对路径，如：/api/images/goods/goods_1_xxx.jpg
 * @returns {string} 完整URL
 * 
 * @example
 * import { getImageUrl } from '@/utils/image';
 * 
 * // 使用方式
 * const fullUrl = getImageUrl('/api/images/goods/goods_1_xxx.jpg');
 * // 返回: http://localhost:3000/api/images/goods/goods_1_xxx.jpg
 * 
 * // 在模板中使用
 * <img :src="getImageUrl(product.imageUrl)" />
 */
export function getImageUrl(relativePath) {
  if (!relativePath) return '';
  
  // 如果已经是完整URL，直接返回
  if (relativePath.startsWith('http://') || relativePath.startsWith('https://')) {
    return relativePath;
  }
  
  // 拼接完整URL
  return IMAGE_BASE_URL + relativePath;
}

/**
 * 批量获取图片URL
 * @param {Array<string>} relativePaths - 相对路径数组
 * @returns {Array<string>} 完整URL数组
 * 
 * @example
 * const urls = getImageUrls(['/api/images/goods/1.jpg', '/api/images/goods/2.jpg']);
 */
export function getImageUrls(relativePaths) {
  if (!Array.isArray(relativePaths)) return [];
  return relativePaths.map(path => getImageUrl(path));
}

/**
 * 获取图片基础URL
 * @returns {string} 图片基础URL
 */
export function getImageBaseUrl() {
  return IMAGE_BASE_URL;
}

export default {
  getImageUrl,
  getImageUrls,
  getImageBaseUrl
};
