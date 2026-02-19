import { get, post, put, del, safeRequest, safeRequestData } from '@/utils/request';

/**
 * 获取用户购物车列表
 * @returns {Promise} 返回购物车列表响应对象
 */
export const getCartList = () => {
  return get('/cart/list');
};

/**
 * 安全获取用户购物车列表
 * @returns {Promise} 成功返回购物车列表，失败返回 null
 */
export const getCartListSafe = () => {
  return safeRequestData(getCartList());
};

/**
 * 添加商品到购物车
 * @param {Number} commodityId 商品ID
 * @param {Number} quantity 数量，默认1
 * @returns {Promise} 返回响应对象
 */
export const addToCart = (commodityId, quantity = 1) => {
  return post(`/cart/add?commodityId=${commodityId}&quantity=${quantity}`);
};

/**
 * 安全添加商品到购物车
 * @param {Number} commodityId 商品ID
 * @param {Number} quantity 数量，默认1
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const addToCartSafe = (commodityId, quantity = 1) => {
  return safeRequestData(addToCart(commodityId, quantity));
};

/**
 * 更新购物车商品数量
 * @param {Number} cartItemId 购物车项ID
 * @param {Number} quantity 新数量
 * @returns {Promise} 返回响应对象
 */
export const updateCartQuantity = (cartItemId, quantity) => {
  return put(`/cart/quantity?cartItemId=${cartItemId}&quantity=${quantity}`);
};

/**
 * 安全更新购物车商品数量
 * @param {Number} cartItemId 购物车项ID
 * @param {Number} quantity 新数量
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const updateCartQuantitySafe = (cartItemId, quantity) => {
  return safeRequestData(updateCartQuantity(cartItemId, quantity));
};

/**
 * 更新购物车商品选中状态
 * @param {Number} cartItemId 购物车项ID
 * @param {Boolean} checked 是否选中
 * @returns {Promise} 返回响应对象
 */
export const updateCartChecked = (cartItemId, checked) => {
  return put(`/cart/checked?cartItemId=${cartItemId}&checked=${checked}`);
};

/**
 * 安全更新购物车商品选中状态
 * @param {Number} cartItemId 购物车项ID
 * @param {Boolean} checked 是否选中
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const updateCartCheckedSafe = (cartItemId, checked) => {
  return safeRequestData(updateCartChecked(cartItemId, checked));
};

/**
 * 删除购物车商品
 * @param {Number} cartItemId 购物车项ID
 * @returns {Promise} 返回响应对象
 */
export const deleteCartItem = (cartItemId) => {
  return del(`/cart/delete?cartItemId=${cartItemId}`);
};

/**
 * 安全删除购物车商品
 * @param {Number} cartItemId 购物车项ID
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const deleteCartItemSafe = (cartItemId) => {
  return safeRequestData(deleteCartItem(cartItemId));
};

/**
 * 清空购物车
 * @returns {Promise} 返回响应对象
 */
export const clearCart = () => {
  return del('/cart/clear');
};

/**
 * 安全清空购物车
 * @returns {Promise} 成功返回数据，失败返回 null
 */
export const clearCartSafe = () => {
  return safeRequestData(clearCart());
};
