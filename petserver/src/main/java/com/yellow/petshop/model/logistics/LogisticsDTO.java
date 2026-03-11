package com.yellow.petshop.model.logistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流信息DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsDTO {
    private Long orderId;
    private String shippingCompany;
    private String trackingNo;
    private String remark;
}
