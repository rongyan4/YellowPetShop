package com.yellow.petshop.model.merchant;

import com.yellow.petshop.model.order.OrderItemVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家端订单详情VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVO {
    private Long id;
    private String orderSn;
    private Long userId;
    private String userName;
    private String userPhone;
    private BigDecimal totalAmount;
    private BigDecimal originalAmount;
    private Boolean priceModified;
    private BigDecimal postage;
    private BigDecimal payAmount;
    private String paymentMethod;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private String shippingStatus;
    private String trackingNo;
    private LocalDateTime shippingTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private List<OrderItemVO> orderItems;
}
