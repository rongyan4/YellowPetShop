package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.wallet.UserWallet;
import com.yellow.petshop.service.WalletService;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 钱包控制器
 */
@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 获取当前用户钱包信息
     */
    @GetMapping("/info")
    public Result<UserWallet> getCurrentWalletInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            UserWallet wallet = walletService.getWalletByUserId(userId);
            return Result.success(wallet);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 设置支付密码
     */
    @PostMapping("/set-pay-password")
    public Result<String> setPayPassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        String password = data.get("password");
        if (password == null || password.length() != 6) {
            return Result.error("支付密码必须为6位数字");
        }
        
        if (!password.matches("\\d{6}")) {
            return Result.error("支付密码必须为6位数字");
        }
        
        try {
            walletService.setPayPassword(userId, password);
            return Result.success("设置成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改支付密码
     */
    @PostMapping("/update-pay-password")
    public Result<String> updatePayPassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        
        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.error("请输入原支付密码");
        }
        
        if (newPassword == null || newPassword.length() != 6) {
            return Result.error("新支付密码必须为6位数字");
        }
        
        if (!newPassword.matches("\\d{6}")) {
            return Result.error("新支付密码必须为6位数字");
        }
        
        try {
            walletService.updatePayPassword(userId, oldPassword, newPassword);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 验证支付密码
     */
    @PostMapping("/verify-pay-password")
    public Result<Boolean> verifyPayPassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        String password = data.get("password");
        if (password == null || password.isEmpty()) {
            return Result.error("请输入支付密码");
        }
        
        try {
            boolean isValid = walletService.verifyPayPassword(userId, password);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取钱包交易记录
     */
    @GetMapping("/transactions")
    public Result<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            Map<String, Object> result = walletService.getTransactions(userId, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 充值
     */
    @PostMapping("/recharge")
    public Result<String> recharge(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            walletService.recharge(userId, data);
            return Result.success("充值成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 提现
     */
    @PostMapping("/withdraw")
    public Result<String> withdraw(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            walletService.withdraw(userId, data);
            return Result.success("提现申请已提交");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询当前用户是否已设置支付密码
     */
    @GetMapping("/has-pay-password")
    public Result<Boolean> hasPayPassword(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        try {
            boolean hasPassword = walletService.hasPayPassword(userId);
            return Result.success(hasPassword);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定用户钱包信息（此路由需放在所有具体路径之后，避免路由冲突）
     */
    @GetMapping("/{userId}")
    public Result<UserWallet> getWalletInfo(@PathVariable Long userId) {
        try {
            UserWallet wallet = walletService.getWalletByUserId(userId);
            return Result.success(wallet);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
