package com.yellow.petshop.model.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情视图对象（包含图片列表）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommodityDetailVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String unit;
    private Integer sold;
    private Integer stock;
    private String mainPicUrl;
    private String msg;
    private String detail;
    private Boolean isValid;
    private String shippingOrigin;
    private BigDecimal postage;
    private List<String> images;
}
