package com.yellow.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yellow.petshop.mapper.MerchantLoginLogMapper;
import com.yellow.petshop.mapper.MerchantMapper;
import com.yellow.petshop.model.merchant.Merchant;
import com.yellow.petshop.model.merchant.MerchantLoginLog;
import com.yellow.petshop.model.merchant.MerchantVO;
import com.yellow.petshop.util.BCryptUtil;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 商家服务类
 */
@Service
public class MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantLoginLogMapper loginLogMapper;

    /**
     * 商家登录
     */
    public String login(String username, String password, String ipAddress, String userAgent) {
        // 查询商家
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUsername, username);
        Merchant merchant = merchantMapper.selectOne(wrapper);

        // 记录登录日志
        MerchantLoginLog loginLog = new MerchantLoginLog();
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setIpAddress(ipAddress);
        loginLog.setUserAgent(userAgent);

        if (merchant == null) {
            loginLog.setMerchantId(0L);
            loginLog.setLoginStatus(0);
            loginLogMapper.insert(loginLog);
            throw new RuntimeException("商家账号不存在");
        }

        if (merchant.getStatus() == 0) {
            loginLog.setMerchantId(merchant.getId());
            loginLog.setLoginStatus(0);
            loginLogMapper.insert(loginLog);
            throw new RuntimeException("商家账号已被禁用");
        }

        // 验证密码
        if (!BCryptUtil.checkPassword(password, merchant.getPassword())) {
            loginLog.setMerchantId(merchant.getId());
            loginLog.setLoginStatus(0);
            loginLogMapper.insert(loginLog);
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Merchant::getId, merchant.getId())
                .set(Merchant::getLastLoginTime, LocalDateTime.now());
        merchantMapper.update(null, updateWrapper);

        // 记录成功登录日志
        loginLog.setMerchantId(merchant.getId());
        loginLog.setLoginStatus(1);
        loginLogMapper.insert(loginLog);

        // 生成双 Token：RT（7天）存 Cookie，AT（2分钟）返回给前端存 localStorage
        // 此处仍返回 RT，MerchantController 会同时调用 generateMerchantAccessToken
        return JwtUtil.generateToken(merchant.getId(), merchant.getUsername());
    }

    /**
     * 根据ID获取商家信息
     */
    public MerchantVO getMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        MerchantVO vo = new MerchantVO();
        BeanUtils.copyProperties(merchant, vo);
        return vo;
    }

    /**
     * 修改商家密码
     */
    public void updatePassword(Long merchantId, String oldPassword, String newPassword) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        // 验证旧密码
        if (!BCryptUtil.checkPassword(oldPassword, merchant.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        LambdaUpdateWrapper<Merchant> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Merchant::getId, merchantId)
                .set(Merchant::getPassword, BCryptUtil.hashPassword(newPassword));
        merchantMapper.update(null, wrapper);
    }
}
