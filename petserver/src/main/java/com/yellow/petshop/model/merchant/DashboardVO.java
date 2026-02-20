package com.yellow.petshop.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商家端数据概览VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardVO {
    /**
     * 今日订单数
     */
    private Integer todayOrderCount;
    
    /**
     * 今日销售额
     */
    private BigDecimal todaySales;
    
    /**
     * 待处理订单数
     */
    private Integer pendingOrderCount;
    
    /**
     * 待发货订单数
     */
    private Integer toShipOrderCount;
    
    /**
     * 总商品数
     */
    private Integer totalProductCount;
    
    /**
     * 上架商品数
     */
    private Integer onSaleProductCount;
    
    /**
     * 下架商品数
     */
    private Integer offSaleProductCount;
    
    /**
     * 总销售额
     */
    private BigDecimal totalSales;
    
    /**
     * 总订单数
     */
    private Integer totalOrderCount;
    
    /**
     * 待评价订单数
     */
    private Integer toCommentOrderCount;
    
    /**
     * 总评论数
     */
    private Integer totalCommentCount;
    
    /**
     * 待回复评论数
     */
    private Integer toReplyCommentCount;
}
