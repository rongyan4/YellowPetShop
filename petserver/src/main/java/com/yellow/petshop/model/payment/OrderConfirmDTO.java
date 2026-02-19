package com.yellow.petshop.model.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmDTO {
    private List<OrderItemDTO> items; // 订单商品列表
    private Long addressId; // 地址ID
    private String paymentMethod; // 支付方式: WALLET, WECHAT, ALIPAY
    private String remark; // 订单备注
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO {
        private Long commodityId; // 商品ID
        private Integer quantity; // 数量
    }
}
