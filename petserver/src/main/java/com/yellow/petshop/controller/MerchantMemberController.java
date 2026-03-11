package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.service.MerchantMemberService;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商家会员管理控制器
 */
@RestController
@RequestMapping("/api/merchant/members")
public class MerchantMemberController {
    
    @Autowired
    private MerchantMemberService memberService;
    

    /**
     * 获取会员列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getMemberList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = memberService.getMemberList(page, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取会员详情
     */
    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getMemberDetail(@PathVariable Long userId) {
        try {
            Map<String, Object> detail = memberService.getMemberDetail(userId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新会员信息
     */
    @PutMapping("/{userId}")
    public Result<String> updateMemberInfo(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> data) {
        try {
            memberService.updateMemberInfo(userId, data);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 调整会员余额
     */
    @PostMapping("/{userId}/balance")
    public Result<String> adjustMemberBalance(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            memberService.adjustMemberBalance(userId, data, merchantId);
            return Result.success("调整成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置会员支付密码
     */
    @PostMapping("/{userId}/reset-pay-password")
    public Result<String> resetMemberPayPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> data) {
        try {
            String newPassword = data.get("newPassword");
            if (newPassword == null || newPassword.length() != 6) {
                return Result.error("支付密码必须为6位数字");
            }
            memberService.resetMemberPayPassword(userId, newPassword);
            return Result.success("重置成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取会员订单列表
     */
    @GetMapping("/{userId}/orders")
    public Result<Map<String, Object>> getMemberOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> result = memberService.getMemberOrders(userId, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新会员启用/禁用状态
     */
    @PutMapping("/status")
    public Result<String> updateMemberStatus(
            @RequestParam Long userId,
            @RequestParam Integer status) {
        try {
            memberService.updateMemberStatus(userId, status);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除会员（逻辑删除）
     */
    @DeleteMapping("/delete/{userId}")
    public Result<String> deleteMember(@PathVariable Long userId) {
        try {
            memberService.deleteMember(userId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从Token中获取商家ID
     */
    private Long getMerchantIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.getUserIdFromToken(token);
    }
}
