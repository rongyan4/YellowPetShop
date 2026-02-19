package com.yellow.petshop.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品明细实体类
 */
@TableName("order_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long commodityId;
    private String commodityName;
    private String commodityPic;
    private BigDecimal commodityPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime createTime;
}
