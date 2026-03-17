package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.wallet.WalletVO;
import com.yellow.petshop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController extends BaseController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 获取钱包信息
     */
    @GetMapping("/wallet")
    public Result<WalletVO> getWalletInfo(HttpServletRequest request) {
        Long userId = getUserId(request);
        WalletVO wallet = paymentService.getWalletInfo(userId);
        return Result.success(wallet);
    }
}
