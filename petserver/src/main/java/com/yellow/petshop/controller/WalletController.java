package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.wallet.UserWallet;
import com.yellow.petshop.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 钱包控制器
 */
@RestController
@RequestMapping("/api/wallet")
public class WalletController extends BaseController {

    @Autowired
    private WalletService walletService;

    /**
     * 获取当前用户钱包信息
     */
    @GetMapping("/info")
    public Result<UserWallet> getCurrentWalletInfo(HttpServletRequest request) {
        Long userId = getUserId(request);
        UserWallet wallet = walletService.getWalletByUserId(userId);
        return Result.success(wallet);
    }

    /**
     * 设置支付密码
     */
    @PostMapping("/set-pay-password")
    public Result<String> setPayPassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        String password = data.get("password");
        if (password == null || password.length() != 6) {
            return Result.error("支付密码必须为6位数字");
        }
        if (!password.matches("\\d{6}")) {
            return Result.error("支付密码必须为6位数字");
        }
        walletService.setPayPassword(userId, password);
        return Result.success("设置成功");
    }

    /**
     * 修改支付密码
     */
    @PostMapping("/update-pay-password")
    public Result<String> updatePayPassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        Long userId = getUserId(request);
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
        walletService.updatePayPassword(userId, oldPassword, newPassword);
        return Result.success("修改成功");
    }

    /**
     * 验证支付密码
     */
    @PostMapping("/verify-pay-password")
    public Result<Boolean> verifyPayPassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        String password = data.get("password");
        if (password == null || password.isEmpty()) {
            return Result.error("请输入支付密码");
        }
        boolean isValid = walletService.verifyPayPassword(userId, password);
        return Result.success(isValid);
    }

    /**
     * 获取钱包交易记录
     */
    @GetMapping("/transactions")
    public Result<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        Map<String, Object> result = walletService.getTransactions(userId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 充值
     */
    @PostMapping("/recharge")
    public Result<String> recharge(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        walletService.recharge(userId, data);
        return Result.success("充值成功");
    }

    /**
     * 提现
     */
    @PostMapping("/withdraw")
    public Result<String> withdraw(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        walletService.withdraw(userId, data);
        return Result.success("提现申请已提交");
    }

    /**
     * 查询当前用户是否已设置支付密码
     */
    @GetMapping("/has-pay-password")
    public Result<Boolean> hasPayPassword(HttpServletRequest request) {
        Long userId = getUserId(request);
        boolean hasPassword = walletService.hasPayPassword(userId);
        return Result.success(hasPassword);
    }

    /**
     * 获取指定用户钱包信息（此路由需放在所有具体路径之后，避免路由冲突）
     */
    @GetMapping("/{userId}")
    public Result<UserWallet> getWalletInfo(@PathVariable Long userId) {
        UserWallet wallet = walletService.getWalletByUserId(userId);
        return Result.success(wallet);
    }
}
