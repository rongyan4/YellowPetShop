package com.yellow.petshop.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单商品明细VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVO {
    private Long id;
    private Long orderId;
    private Long commodityId;
    private String commodityName;
    private String commodityPic;
    private BigDecimal commodityPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}
