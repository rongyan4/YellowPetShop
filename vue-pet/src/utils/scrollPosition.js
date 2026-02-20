/**
 * 页面滚动位置管理工具
 * 用于保存和恢复页面滚动位置
 */

const scrollPositions = new Map();

/**
 * 保存页面滚动位置
 * @param {string} key - 页面标识符（通常使用路由路径）
 * @param {number} position - 滚动位置
 */
export function saveScrollPosition(key, position) {
  scrollPositions.set(key, position);
  console.log(`保存滚动位置: ${key} = ${position}`);
}

/**
 * 获取页面滚动位置
 * @param {string} key - 页面标识符
 * @returns {number} 滚动位置，如果不存在则返回0
 */
export function getScrollPosition(key) {
  const position = scrollPositions.get(key) || 0;
  console.log(`获取滚动位置: ${key} = ${position}`);
  return position;
}

/**
 * 清除页面滚动位置
 * @param {string} key - 页面标识符
 */
export function clearScrollPosition(key) {
  scrollPositions.delete(key);
  console.log(`清除滚动位置: ${key}`);
}

/**
 * 清除所有滚动位置
 */
export function clearAllScrollPositions() {
  scrollPositions.clear();
  console.log('清除所有滚动位置');
}

/**
 * 恢复页面滚动位置
 * @param {string} key - 页面标识符
 * @param {boolean} smooth - 是否平滑滚动
 */
export function restoreScrollPosition(key, smooth = false) {
  const position = getScrollPosition(key);
  if (position > 0) {
    setTimeout(() => {
      window.scrollTo({
        top: position,
        behavior: smooth ? 'smooth' : 'auto'
      });
    }, 0);
  }
}
