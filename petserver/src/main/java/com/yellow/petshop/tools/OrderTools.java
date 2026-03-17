package com.yellow.petshop.tools;

import com.yellow.petshop.model.order.OrderVO;
import com.yellow.petshop.service.OrderService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Component
public class OrderTools {

    @Autowired
    private OrderService orderService;

    /**
     * 从请求头中获取用户ID
     */
    private Long getUserIdFromToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String token = attributes.getRequest().getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    String jwtToken = token.substring(7);
                    return JwtUtil.getUserIdFromToken(jwtToken);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("无法获取用户信息，请确保已登录");
        }
        throw new RuntimeException("无法获取用户信息，请确保已登录");
    }

    @Tool(name = "query_all_orders",
          description = "查询当前用户的所有订单")
    public List<OrderVO> queryAllOrders() {
        Long userId = getUserIdFromToken();
        return orderService.getOrdersByUserId(userId);
    }

    @Tool(name = "query_orders_by_status",
          description = "根据订单状态查询用户的订单，状态包括：PENDING(待付款)、PAID(已付款)、SHIPPED(已发货)、COMPLETED(已完成)、CANCELLED(已取消)、ALL(所有订单)")
    public List<OrderVO> queryOrdersByStatus(
            @ToolParam(description = "订单状态：PENDING、PAID、SHIPPED、COMPLETED、CANCELLED、ALL") String status) {
        Long userId = getUserIdFromToken();
        return orderService.getOrdersByUserIdAndStatus(userId, status);
    }

    @Tool(name = "get_order_detail",
          description = "获取订单详情")
    public OrderVO getOrderDetail(
            @ToolParam(description = "订单ID") Long orderId) {
        Long userId = getUserIdFromToken();
        return orderService.getOrderDetail(orderId, userId);
    }

    @Tool(name = "cancel_order",
          description = "取消订单，只有待付款状态的订单可以取消")
    public Boolean cancelOrder(
            @ToolParam(description = "订单ID") Long orderId) {
        Long userId = getUserIdFromToken();
        return orderService.cancelOrder(orderId, userId);
    }

    @Tool(name = "delete_order",
          description = "删除订单，只有已取消或已完成的订单可以删除")
    public Boolean deleteOrder(
            @ToolParam(description = "订单ID") Long orderId) {
        Long userId = getUserIdFromToken();
        return orderService.deleteOrder(orderId, userId);
    }

    @Tool(name = "confirm_receipt",
          description = "确认收货，只有已发货状态的订单可以确认收货")
    public Boolean confirmReceipt(
            @ToolParam(description = "订单ID") Long orderId) {
        Long userId = getUserIdFromToken();
        return orderService.confirmReceipt(orderId, userId);
    }
}
