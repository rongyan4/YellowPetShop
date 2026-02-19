package com.yellow.petshop.service;

import com.yellow.petshop.model.order.CreateOrderDTO;
import com.yellow.petshop.model.order.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param userId 用户ID
     * @param createOrderDTO 创建订单请求
     * @return 订单详情
     */
    OrderVO createOrder(Long userId, CreateOrderDTO createOrderDTO);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderVO> getOrdersByUserId(Long userId);
    
    /**
     * 根据状态获取用户订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderVO> getOrdersByUserIdAndStatus(Long userId, String status);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId, Long userId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    Boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    Boolean deleteOrder(Long orderId, Long userId);
    
    /**
     * 确认收货
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    Boolean confirmReceipt(Long orderId, Long userId);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param paymentMethod 支付方式
     * @param payPassword 支付密码（钱包支付时需要）
     * @return 是否成功
     */
    Boolean payOrder(Long orderId, Long userId, String paymentMethod, String payPassword);

}
