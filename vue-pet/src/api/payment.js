import { get, post, safeRequestData } from '@/utils/request';

/**
 * 获取钱包信息
 */
export const getWalletInfo = () => {
  return get('/payment/wallet');
};

export const getWalletInfoSafe = () => {
  return safeRequestData(getWalletInfo());
};

