package com.yellow.petshop.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.OrderMapper;
import com.yellow.petshop.model.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时处理定时任务
 * 每分钟执行一次，检查并取消超时未支付的订单
 */
@Component
public class OrderTimeoutTask {
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 订单超时时间（分钟）
     */
    private static final int TIMEOUT_MINUTES = 5;
    
    /**
     * 每分钟执行一次，检查超时订单
     * cron表达式: 秒 分 时 日 月 周
     * 0 * * * * ? 表示每分钟的第0秒执行
     */
    @Scheduled(cron = "0 * * * * ?")
    public void cancelTimeoutOrders() {
        try {
            System.out.println("=== 开始检查超时订单 ===");
            System.out.println("检查时间: " + LocalDateTime.now());
            
            // 计算超时时间点
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);
            System.out.println("超时时间点: " + timeoutTime);
            
            // 查询所有待支付且创建时间超过5分钟的订单
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "PENDING")
                       .lt("create_time", timeoutTime);
            
            List<Order> timeoutOrders = orderMapper.selectList(queryWrapper);
            
            if (timeoutOrders.isEmpty()) {
                System.out.println("没有超时订单");
                return;
            }
            
            System.out.println("发现 " + timeoutOrders.size() + " 个超时订单");
            
            // 批量取消超时订单
            int cancelCount = 0;
            for (Order order : timeoutOrders) {
                try {
                    order.setStatus("CANCELLED");
                    order.setUpdateTime(LocalDateTime.now());
                    orderMapper.updateById(order);
                    cancelCount++;
                    
                    System.out.println("订单 " + order.getOrderSn() + " 已自动取消（超时未支付）");
                } catch (Exception e) {
                    System.err.println("取消订单 " + order.getOrderSn() + " 失败: " + e.getMessage());
                }
            }
            
            System.out.println("成功取消 " + cancelCount + " 个超时订单");
            System.out.println("=== 超时订单检查完成 ===");
            
        } catch (Exception e) {
            System.err.println("检查超时订单时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
