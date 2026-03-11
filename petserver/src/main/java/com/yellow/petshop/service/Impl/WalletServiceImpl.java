package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.UserWalletMapper;
import com.yellow.petshop.mapper.WalletTransactionMapper;
import com.yellow.petshop.model.wallet.UserWallet;
import com.yellow.petshop.model.wallet.WalletTransaction;
import com.yellow.petshop.service.WalletService;
import com.yellow.petshop.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钱包服务实现类
 */
@Service
public class WalletServiceImpl implements WalletService {
    
    @Autowired
    private UserWalletMapper walletMapper;
    
    @Autowired
    private WalletTransactionMapper transactionMapper;
    
    /**
     * 内部使用：获取钱包（含支付密码，不对外暴露）
     */
    private UserWallet getWalletWithPassword(Long userId) {
        QueryWrapper<UserWallet> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserWallet wallet = walletMapper.selectOne(wrapper);
        if (wallet == null) {
            wallet = createWallet(userId);
        }
        return wallet;
    }

    @Override
    public UserWallet getWalletByUserId(Long userId) {
        UserWallet wallet = getWalletWithPassword(userId);
        // 隐藏支付密码，不对外返回
        wallet.setPayPassword(null);
        return wallet;
    }
    
    @Override
    @Transactional
    public UserWallet createWallet(Long userId) {
        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setIsLocked(false);
        wallet.setErrorCount(0);
        wallet.setCreateTime(LocalDateTime.now());
        walletMapper.insert(wallet);
        return wallet;
    }
    
    @Override
    @Transactional
    public void setPayPassword(Long userId, String password) {
        UserWallet wallet = getWalletWithPassword(userId);
        
        if (wallet.getPayPassword() != null && !wallet.getPayPassword().isEmpty()) {
            throw new RuntimeException("支付密码已设置，请使用修改密码功能");
        }
        
        String encryptedPassword = BCryptUtil.encrypt(password);
        wallet.setPayPassword(encryptedPassword);
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
    }
    
    @Override
    @Transactional
    public void updatePayPassword(Long userId, String oldPassword, String newPassword) {
        UserWallet wallet = getWalletWithPassword(userId);
        
        if (wallet.getPayPassword() == null || wallet.getPayPassword().isEmpty()) {
            throw new RuntimeException("尚未设置支付密码");
        }
        
        // 验证原密码
        if (!BCryptUtil.verify(oldPassword, wallet.getPayPassword())) {
            throw new RuntimeException("原支付密码错误");
        }
        
        // 设置新密码
        String encryptedPassword = BCryptUtil.encrypt(newPassword);
        wallet.setPayPassword(encryptedPassword);
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
    }
    
    @Override
    public boolean verifyPayPassword(Long userId, String password) {
        UserWallet wallet = getWalletWithPassword(userId);
        
        if (wallet.getPayPassword() == null || wallet.getPayPassword().isEmpty()) {
            throw new RuntimeException("尚未设置支付密码");
        }
        
        // 检查是否被锁定
        if (Boolean.TRUE.equals(wallet.getIsLocked())) {
            if (wallet.getLockTime() != null) {
                // 检查锁定时间是否已过（30分钟）
                LocalDateTime unlockTime = wallet.getLockTime().plusMinutes(30);
                if (LocalDateTime.now().isBefore(unlockTime)) {
                    throw new RuntimeException("账户已被锁定，请30分钟后再试");
                } else {
                    // 解锁
                    wallet.setIsLocked(false);
                    wallet.setErrorCount(0);
                    wallet.setLockTime(null);
                    walletMapper.updateById(wallet);
                }
            }
        }
        
        boolean isValid = BCryptUtil.verify(password, wallet.getPayPassword());
        
        if (!isValid) {
            // 密码错误，增加错误次数
            int errorCount = wallet.getErrorCount() == null ? 0 : wallet.getErrorCount();
            errorCount++;
            wallet.setErrorCount(errorCount);
            
            // 错误次数达到5次，锁定账户
            if (errorCount >= 5) {
                wallet.setIsLocked(true);
                wallet.setLockTime(LocalDateTime.now());
                walletMapper.updateById(wallet);
                throw new RuntimeException("密码错误次数过多，账户已被锁定30分钟");
            }
            
            walletMapper.updateById(wallet);
            throw new RuntimeException("支付密码错误，还可尝试" + (5 - errorCount) + "次");
        } else {
            // 密码正确，重置错误次数
            if (wallet.getErrorCount() != null && wallet.getErrorCount() > 0) {
                wallet.setErrorCount(0);
                walletMapper.updateById(wallet);
            }
        }
        
        return true;
    }
    
    @Override
    public Map<String, Object> getTransactions(Long userId, int page, int pageSize) {
        QueryWrapper<WalletTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        
        // 查询总数
        Long total = transactionMapper.selectCount(wrapper);
        
        // 分页查询
        int offset = (page - 1) * pageSize;
        wrapper.last("LIMIT " + offset + ", " + pageSize);
        List<WalletTransaction> list = transactionMapper.selectList(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    @Override
    @Transactional
    public void recharge(Long userId, Map<String, Object> data) {
        Object amountObj = data.get("amount");
        BigDecimal amount;
        
        if (amountObj instanceof String) {
            amount = new BigDecimal((String) amountObj);
        } else if (amountObj instanceof Number) {
            amount = BigDecimal.valueOf(((Number) amountObj).doubleValue());
        } else {
            throw new RuntimeException("金额格式错误");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        
        // 更新钱包余额
        UserWallet wallet = getWalletWithPassword(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("RECHARGE");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(wallet.getBalance().subtract(amount));
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setRemark("充值");
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
    }
    
    @Override
    @Transactional
    public void withdraw(Long userId, Map<String, Object> data) {
        Object amountObj = data.get("amount");
        BigDecimal amount;
        
        if (amountObj instanceof String) {
            amount = new BigDecimal((String) amountObj);
        } else if (amountObj instanceof Number) {
            amount = BigDecimal.valueOf(((Number) amountObj).doubleValue());
        } else {
            throw new RuntimeException("金额格式错误");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }
        
        // 验证支付密码
        String password = (String) data.get("password");
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("请输入支付密码");
        }
        verifyPayPassword(userId, password);
        
        // 检查余额
        UserWallet wallet = getWalletWithPassword(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        // 更新钱包余额
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("WITHDRAW");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(wallet.getBalance().add(amount));
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setRemark("提现");
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
    }
    
    @Override
    @Transactional
    public void deduct(Long userId, BigDecimal amount, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("扣款金额必须大于0");
        }
        
        UserWallet wallet = getWalletWithPassword(userId);
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("DEDUCT");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setRemark(remark);
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
    }
    
    @Override
    public boolean hasPayPassword(Long userId) {
        UserWallet wallet = getWalletWithPassword(userId);
        return wallet.getPayPassword() != null && !wallet.getPayPassword().isEmpty();
    }

    @Override
    @Transactional
    public void addBalance(Long userId, BigDecimal amount, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("增加金额必须大于0");
        }
        
        UserWallet wallet = getWalletWithPassword(userId);
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("ADD");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setRemark(remark);
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
    }
}
