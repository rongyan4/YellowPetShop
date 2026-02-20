package com.yellow.petshop.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 修改订单价格DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderPriceDTO {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 新的总金额
     */
    private BigDecimal newTotalAmount;
    
    /**
     * 新的邮费
     */
    private BigDecimal newPostage;
    
    /**
     * 修改原因
     */
    private String reason;
}
