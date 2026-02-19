package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.order.CreateOrderDTO;
import com.yellow.petshop.model.order.OrderVO;
import com.yellow.petshop.model.payment.PaymentDTO;
import com.yellow.petshop.service.OrderService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     * 访问路径: POST /api/order/create
     * 需要登录
     */
    @PostMapping("/create")
    public Result<OrderVO> createOrder(
            @RequestBody CreateOrderDTO createOrderDTO,
            HttpServletRequest request) {
        
        long requestId = System.currentTimeMillis();
        System.out.println("=== Controller: 收到创建订单请求 [ID: " + requestId + "] ===");
        System.out.println("1. 请求体: " + createOrderDTO);
        System.out.println("   请求时间: " + new java.util.Date());
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("ERROR: 未登录");
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            System.out.println("ERROR: token无效");
            return Result.error("token无效");
        }
        
        System.out.println("2. 用户ID: " + userId);
        
        try {
            OrderVO orderVO = orderService.createOrder(userId, createOrderDTO);
            System.out.println("3. 订单创建成功: " + orderVO.getId());
            System.out.println("   返回时间: " + new java.util.Date());
            System.out.println("=== Controller: 请求处理完成 [ID: " + requestId + "] ===");
            return Result.success(orderVO);
        } catch (Exception e) {
            System.out.println("ERROR: 订单创建失败 [ID: " + requestId + "]");
            System.out.println("   异常类型: " + e.getClass().getName());
            System.out.println("   异常消息: " + e.getMessage());
            System.out.println("   异常时间: " + new java.util.Date());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户订单列表
     * 访问路径: GET /api/order/list
     * 需要登录
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            List<OrderVO> orders;
            if (status != null && !status.isEmpty()) {
                orders = orderService.getOrdersByUserIdAndStatus(userId, status);
            } else {
                orders = orderService.getOrdersByUserId(userId);
            }
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     * 访问路径: GET /api/order/detail/{orderId}
     * 需要登录
     */
    @GetMapping("/detail/{orderId}")
    public Result<OrderVO> getOrderDetail(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            OrderVO orderVO = orderService.getOrderDetail(orderId, userId);
            return Result.success(orderVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取消订单
     * 访问路径: PUT /api/order/cancel/{orderId}
     * 需要登录
     */
    @PutMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            Boolean success = orderService.cancelOrder(orderId, userId);
            if (success) {
                return Result.success("取消成功");
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除订单
     * 访问路径: DELETE /api/order/delete/{orderId}
     * 需要登录
     */
    @DeleteMapping("/delete/{orderId}")
    public Result<String> deleteOrder(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            Boolean success = orderService.deleteOrder(orderId, userId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 确认收货
     * 访问路径: PUT /api/order/confirm/{orderId}
     * 需要登录
     */
    @PutMapping("/confirm/{orderId}")
    public Result<String> confirmReceipt(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            Boolean success = orderService.confirmReceipt(orderId, userId);
            if (success) {
                return Result.success("确认收货成功");
            } else {
                return Result.error("确认收货失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 支付订单
     * 访问路径: POST /api/order/pay/{orderId}
     * 需要登录
     */
    @PostMapping("/pay/{orderId}")
    public Result<String> payOrder(
            @PathVariable Long orderId,
            @RequestBody PaymentDTO paymentDTO,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            Boolean success = orderService.payOrder(
                orderId, 
                userId, 
                paymentDTO.getPaymentMethod(), 
                paymentDTO.getPayPassword()
            );
            
            if (success) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}
