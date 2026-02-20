package com.yellow.petshop.service;

import com.yellow.petshop.model.merchant.DashboardVO;
import com.yellow.petshop.model.merchant.OrderDetailVO;
import com.yellow.petshop.model.merchant.ShipOrderDTO;
import com.yellow.petshop.model.merchant.UpdateOrderPriceDTO;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.order.OrderVO;

/**
 * 商家订单服务接口
 */
public interface MerchantOrderService {
    
    /**
     * 获取商家数据概览
     * @return 数据概览
     */
    DashboardVO getDashboard();
    
    /**
     * 分页查询订单列表
     * @param status 订单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    PageResult<OrderVO> getOrderList(String status, Integer page, Integer size);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId);
    
    /**
     * 修改待支付订单价格
     * @param dto 修改价格DTO
     */
    void updateOrderPrice(UpdateOrderPriceDTO dto);
    
    /**
     * 发货
     * @param dto 发货DTO
     */
    void shipOrder(ShipOrderDTO dto);
}
