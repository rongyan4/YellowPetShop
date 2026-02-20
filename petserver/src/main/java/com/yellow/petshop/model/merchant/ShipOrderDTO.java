package com.yellow.petshop.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单发货DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipOrderDTO {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 物流公司
     */
    private String shippingCompany;
    
    /**
     * 物流单号
     */
    private String trackingNo;
    
    /**
     * 备注
     */
    private String remark;
}
