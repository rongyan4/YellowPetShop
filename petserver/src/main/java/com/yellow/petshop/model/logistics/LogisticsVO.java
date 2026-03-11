package com.yellow.petshop.model.logistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流信息VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsVO {
    private Long id;
    private String shippingCompany;
    private String trackingNo;
    private String status;
    private String remark;
    private LocalDateTime shippingTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;
}
