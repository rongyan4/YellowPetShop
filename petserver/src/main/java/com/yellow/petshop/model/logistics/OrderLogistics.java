package com.yellow.petshop.model.logistics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单物流映射表实体类
 */
@TableName("order_logistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLogistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long logisticsId;
    private LocalDateTime createTime;
}
