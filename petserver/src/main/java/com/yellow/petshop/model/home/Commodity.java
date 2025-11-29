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
public class Commodity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal price;
    private String unit;
    private Integer sold;
    private String mainPicUrl;
    private String msg;
}
