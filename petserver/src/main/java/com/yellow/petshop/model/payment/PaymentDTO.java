package com.yellow.petshop.model.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long orderId; // 订单ID
    private String paymentMethod; // 支付方式
    private String payPassword; // 支付密码（钱包支付时需要）
}
