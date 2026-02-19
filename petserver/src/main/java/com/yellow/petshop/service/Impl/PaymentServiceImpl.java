package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.*;
import com.yellow.petshop.model.wallet.UserWallet;
import com.yellow.petshop.model.wallet.WalletVO;
import com.yellow.petshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private UserWalletMapper walletMapper;
    
    @Override
    public WalletVO getWalletInfo(Long userId) {
        UserWallet wallet = walletMapper.selectByUserId(userId);
        
        if (wallet == null) {
            // 如果钱包不存在，创建一个
            wallet = new UserWallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setIsLocked(false);
            wallet.setErrorCount(0);
            walletMapper.insert(wallet);
        }
        
        // 检查是否锁定超时（30分钟）
        if (Boolean.TRUE.equals(wallet.getIsLocked()) && wallet.getLockTime() != null) {
            if (LocalDateTime.now().isAfter(wallet.getLockTime().plusMinutes(30))) {
                // 解锁
                wallet.setIsLocked(false);
                wallet.setErrorCount(0);
                wallet.setLockTime(null);
                walletMapper.updateById(wallet);
            }
        }
        
        return WalletVO.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .isLocked(wallet.getIsLocked())
                .hasPayPassword(wallet.getPayPassword() != null && !wallet.getPayPassword().isEmpty())
                .build();
    }
}
