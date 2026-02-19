package com.yellow.petshop.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单预览VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPreviewVO {
    private List<PreviewItemVO> items; // 商品列表
    private BigDecimal totalAmount; // 商品总金额
    private BigDecimal postage; // 邮费
    private BigDecimal payAmount; // 实付金额
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreviewItemVO {
        private Long commodityId;
        private String commodityName;
        private String commodityPic;
        private BigDecimal commodityPrice;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
