package com.yellow.petshop.model.home;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品图片实体类
 */
@TableName("commodity_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long commodityId;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean isMain;
    private LocalDateTime createTime;
}
