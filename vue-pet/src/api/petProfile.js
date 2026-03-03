import request from '@/utils/request';

/**
 * 获取宠物档案列表
 * @returns {Promise}
 */
export const getPetProfileList = () => {
  return request({
    url: '/pet-profile/list',
    method: 'get'
  });
};

/**
 * 获取宠物档案详情
 * @param {number} id - 档案ID
 * @returns {Promise}
 */
export const getPetProfileDetail = (id) => {
  return request({
    url: `/pet-profile/${id}`,
    method: 'get'
  });
};

/**
 * 添加宠物档案
 * @param {Object} data - 档案信息
 * @returns {Promise}
 */
export const addPetProfile = (data) => {
  return request({
    url: '/pet-profile/add',
    method: 'post',
    data
  });
};

/**
 * 更新宠物档案
 * @param {Object} data - 档案信息
 * @returns {Promise}
 */
export const updatePetProfile = (data) => {
  return request({
    url: '/pet-profile/update',
    method: 'put',
    data
  });
};

/**
 * 删除宠物档案
 * @param {number} id - 档案ID
 * @returns {Promise}
 */
export const deletePetProfile = (id) => {
  return request({
    url: `/pet-profile/${id}`,
    method: 'delete'
  });
};

/**
 * 上传宠物头像
 * @param {File} file - 图片文件
 * @returns {Promise}
 */
export const uploadPetAvatar = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/pet-profile/upload-avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 根据宠物档案获取推荐商品
 * @param {number} petId - 宠物档案ID
 * @returns {Promise}
 */
export const getRecommendGoods = (petId) => {
  return request({
    url: `/pet-profile/recommend/${petId}`,
    method: 'get'
  });
};
