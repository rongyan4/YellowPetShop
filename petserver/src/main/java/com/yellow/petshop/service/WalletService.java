package com.yellow.petshop.service;

import com.yellow.petshop.model.wallet.UserWallet;

import java.util.Map;

/**
 * 钱包服务接口
 */
public interface WalletService {
    
    /**
     * 根据用户ID获取钱包信息
     */
    UserWallet getWalletByUserId(Long userId);
    
    /**
     * 创建钱包
     */
    UserWallet createWallet(Long userId);
    
    /**
     * 设置支付密码
     */
    void setPayPassword(Long userId, String password);
    
    /**
     * 修改支付密码
     */
    void updatePayPassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 验证支付密码
     */
    boolean verifyPayPassword(Long userId, String password);
    
    /**
     * 获取交易记录
     */
    Map<String, Object> getTransactions(Long userId, int page, int pageSize);
    
    /**
     * 充值
     */
    void recharge(Long userId, Map<String, Object> data);
    
    /**
     * 提现
     */
    void withdraw(Long userId, Map<String, Object> data);
    
    /**
     * 扣款（用于订单支付等）
     */
    void deduct(Long userId, java.math.BigDecimal amount, String remark);
    
    /**
     * 增加余额（用于退款等）
     */
    void addBalance(Long userId, java.math.BigDecimal amount, String remark);

    /**
     * 判断用户是否已设置支付密码
     */
    boolean hasPayPassword(Long userId);
}
