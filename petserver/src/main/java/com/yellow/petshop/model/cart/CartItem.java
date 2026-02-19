package com.yellow.petshop.model.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 购物车项实体类
 */
@TableName("cart_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long commodityId;
    private Integer quantity;
    private Boolean checked;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
