import request from '@/utils/request';

/**
 * 获取商品列表
 */
export function getMerchantGoodsList(params) {
  return request({
    url: '/merchant/goods/list',
    method: 'get',
    params
  });
}

/**
 * 添加商品
 */
export function addMerchantGoods(data) {
  return request({
    url: '/merchant/goods/add',
    method: 'post',
    data
  });
}

/**
 * 更新商品
 */
export function updateMerchantGoods(data) {
  return request({
    url: '/merchant/goods/update',
    method: 'put',
    data
  });
}

/**
 * 删除商品
 */
export function deleteMerchantGoods(id) {
  return request({
    url: `/merchant/goods/delete/${id}`,
    method: 'delete'
  });
}

/**
 * 批量删除商品
 */
export function batchDeleteMerchantGoods(ids) {
  return request({
    url: '/merchant/goods/batch-delete',
    method: 'delete',
    data: ids
  });
}
