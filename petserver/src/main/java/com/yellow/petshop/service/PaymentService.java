package com.yellow.petshop.service;

import com.yellow.petshop.model.payment.OrderConfirmDTO;
import com.yellow.petshop.model.payment.OrderPreviewVO;
import com.yellow.petshop.model.payment.PaymentDTO;
import com.yellow.petshop.model.wallet.WalletVO;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 获取钱包信息
     */
    WalletVO getWalletInfo(Long userId);
    

}
