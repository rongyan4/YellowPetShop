package com.yellow.petshop.service;

import java.util.Map;

/**
 * 商家会员管理服务接口
 */
public interface MerchantMemberService {
    
    /**
     * 分页获取会员列表
     */
    Map<String, Object> getMemberList(int page, int pageSize, String keyword);
    
    /**
     * 获取会员详情
     */
    Map<String, Object> getMemberDetail(Long userId);
    
    /**
     * 更新会员信息
     */
    void updateMemberInfo(Long userId, Map<String, Object> data);
    
    /**
     * 调整会员余额
     */
    void adjustMemberBalance(Long userId, Map<String, Object> data, Long merchantId);
    
    /**
     * 重置会员支付密码
     */
    void resetMemberPayPassword(Long userId, String newPassword);
    
    /**
     * 获取会员订单列表
     */
    Map<String, Object> getMemberOrders(Long userId, int page, int pageSize);
    
    /**
     * 更新会员启用/禁用状态
     */
    void updateMemberStatus(Long userId, Integer status);
    
    /**
     * 删除会员（逻辑删除：禁用账号）
     */
    void deleteMember(Long userId);
}
