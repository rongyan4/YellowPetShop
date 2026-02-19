package com.yellow.petshop.model.cart;

import com.yellow.petshop.model.home.CommodityInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车视图对象（包含商品信息）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemVO {
    private Long id;
    private Long userId;
    private Long commodityId;
    private Integer quantity;
    private Boolean checked;
    
    // 商品信息
    private String name;
    private BigDecimal price;
    private String unit;
    private String mainPicUrl;
    private String msg;
    private Boolean isValid;
    private BigDecimal postage;
}
