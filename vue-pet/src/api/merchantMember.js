import request from '@/utils/request';

/**
 * 获取会员列表
 */
export function getMerchantMemberList(params) {
  return request({
    url: '/merchant/members/list',
    method: 'get',
    params
  });
}

/**
 * 更新会员状态
 */
export function updateMemberStatus(params) {
  return request({
    url: '/merchant/members/status',
    method: 'put',
    params
  });
}

/**
 * 删除会员
 */
export function deleteMember(id) {
  return request({
    url: `/merchant/members/delete/${id}`,
    method: 'delete'
  });
}
