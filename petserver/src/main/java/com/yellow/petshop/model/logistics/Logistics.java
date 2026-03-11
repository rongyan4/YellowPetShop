package com.yellow.petshop.model.logistics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流信息实体类
 */
@TableName("logistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Logistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String shippingCompany;
    private String trackingNo;
    private String status;
    private String remark;
    private LocalDateTime shippingTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
