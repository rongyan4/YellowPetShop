package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.service.MerchantMemberService;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家会员管理控制器
 */
@RestController
@RequestMapping("/api/merchant/member")
public class MerchantMemberController {

    @Autowired
    private MerchantMemberService memberService;

    /**
     * 分页查询会员列表
     */
    @GetMapping("/list")
    public Result<PageResult<User>> getMemberList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            PageResult<User> result = memberService.getMemberList(page, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新会员状态
     */
    @PutMapping("/status")
    public Result<String> updateMemberStatus(
            @RequestParam Long userId,
            @RequestParam Integer status,
            HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            memberService.updateMemberStatus(userId, status, merchantId, ipAddress);
            return Result.success("会员状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除会员
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteMember(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            memberService.deleteMember(id, merchantId, ipAddress);
            return Result.success("会员删除成功");
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

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
