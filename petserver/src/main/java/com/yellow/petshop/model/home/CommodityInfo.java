package com.yellow.petshop.model.home;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@TableName("commodity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private String unit;
    private Integer sold;
    private String mainPicUrl;
    private String msg;
    private String detail;
    private Boolean isValid;
    private String shippingOrigin;
    private BigDecimal postage;
}
