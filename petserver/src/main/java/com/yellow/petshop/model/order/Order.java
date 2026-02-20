package com.yellow.petshop.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@TableName("orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderSn;
    private Long userId;
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
    private String shippingCompany;
    private String trackingNo;
    private LocalDateTime shippingTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    
    @TableField(exist = false)
    private List<OrderItem> orderItems;
}
