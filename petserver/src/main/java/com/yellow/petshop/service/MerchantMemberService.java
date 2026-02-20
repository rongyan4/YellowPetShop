package com.yellow.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yellow.petshop.mapper.MerchantOperationLogMapper;
import com.yellow.petshop.mapper.UserMapper;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.merchant.MerchantOperationLog;
import com.yellow.petshop.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 商家会员管理服务类
 */
@Service
public class MerchantMemberService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantOperationLogMapper operationLogMapper;

    /**
     * 分页查询会员列表
     */
    public PageResult<User> getMemberList(int page, int pageSize, String keyword) {
        Page<User> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setList(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);
        
        return pageResult;
    }

    /**
     * 更新会员状态
     */
    public void updateMemberStatus(Long userId, Integer status, Long merchantId, String ipAddress) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setIsValid(status);
            userMapper.updateById(user);
            
            // 记录操作日志
            String statusText = status == 1 ? "启用" : "禁用";
            logOperation(merchantId, "会员管理", statusText + "会员：" + user.getUsername(), ipAddress);
        }
    }

    /**
     * 删除会员
     */
    public void deleteMember(Long userId, Long merchantId, String ipAddress) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            userMapper.deleteById(userId);
            // 记录操作日志
            logOperation(merchantId, "会员管理", "删除会员：" + user.getUsername(), ipAddress);
        }
    }

    /**
     * 记录操作日志
     */
    private void logOperation(Long merchantId, String type, String desc, String ipAddress) {
        MerchantOperationLog log = new MerchantOperationLog();
        log.setMerchantId(merchantId);
        log.setOperationType(type);
        log.setOperationDesc(desc);
        log.setOperationTime(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        operationLogMapper.insert(log);
    }
}
