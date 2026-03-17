package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.order.CreateOrderDTO;
import com.yellow.petshop.model.order.OrderVO;
import com.yellow.petshop.model.payment.PaymentDTO;
import com.yellow.petshop.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController extends BaseController {

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
        Long userId = getUserId(request);
        log.debug("创建订单, userId={}, dto={}", userId, createOrderDTO);
        OrderVO orderVO = orderService.createOrder(userId, createOrderDTO);
        return Result.success(orderVO);
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
        Long userId = getUserId(request);
        List<OrderVO> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.getOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderService.getOrdersByUserId(userId);
        }
        return Result.success(orders);
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
        Long userId = getUserId(request);
        OrderVO orderVO = orderService.getOrderDetail(orderId, userId);
        return Result.success(orderVO);
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
        Long userId = getUserId(request);
        Boolean success = orderService.cancelOrder(orderId, userId);
        return success ? Result.success("取消成功") : Result.error("取消失败");
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
        Long userId = getUserId(request);
        Boolean success = orderService.deleteOrder(orderId, userId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
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
        Long userId = getUserId(request);
        Boolean success = orderService.confirmReceipt(orderId, userId);
        return success ? Result.success("确认收货成功") : Result.error("确认收货失败");
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
        Long userId = getUserId(request);
        Boolean success = orderService.payOrder(
                orderId,
                userId,
                paymentDTO.getPaymentMethod(),
                paymentDTO.getPayPassword()
        );
        return success ? Result.success("支付成功") : Result.error("支付失败");
    }
}
