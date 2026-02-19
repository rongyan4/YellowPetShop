package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.payment.OrderConfirmDTO;
import com.yellow.petshop.model.payment.OrderPreviewVO;
import com.yellow.petshop.model.payment.PaymentDTO;
import com.yellow.petshop.model.wallet.WalletVO;
import com.yellow.petshop.service.PaymentService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 获取钱包信息
     */
    @GetMapping("/wallet")
    public Result<WalletVO> getWalletInfo(HttpServletRequest request) {
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
            WalletVO wallet = paymentService.getWalletInfo(userId);
            return Result.success(wallet);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    

    

    

    

    

}
