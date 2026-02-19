import { get, post, put, safeRequestData } from '@/utils/request';

/**
 * 获取用户地址列表
 */
export const getAddressList = () => {
  return get('/address/list');
};

export const getAddressListSafe = () => {
  return safeRequestData(getAddressList());
};

/**
 * 获取默认地址
 */
export const getDefaultAddress = () => {
  return get('/address/default');
};

export const getDefaultAddressSafe = () => {
  return safeRequestData(getDefaultAddress());
};

/**
 * 添加地址
 */
export const addAddress = (address) => {
  return post('/address/add', address);
};

export const addAddressSafe = (address) => {
  return safeRequestData(addAddress(address));
};

/**
 * 设置默认地址
 */
export const setDefaultAddress = (addressId) => {
  return put(`/address/setDefault/${addressId}`);
};

export const setDefaultAddressSafe = (addressId) => {
  return safeRequestData(setDefaultAddress(addressId));
};
