package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.*;
import com.yellow.petshop.model.cart.CartItem;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.order.*;
import com.yellow.petshop.model.payment.PaymentRecord;
import com.yellow.petshop.model.wallet.UserWallet;
import com.yellow.petshop.service.OrderService;
import com.yellow.petshop.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private CommodityMapper commodityMapper;
    
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;
    
    @Autowired
    private WalletMapper walletMapper;
    
    /**
     * 生成订单号
     * 格式: 时间戳 + 用户ID后4位 + 随机数
     */
    private String generateOrderSn(Long userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdSuffix = String.format("%04d", userId % 10000);
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return timestamp + userIdSuffix + random;
    }
    
    /**
     * 获取订单状态文本
     */
    private String getStatusText(String status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("PENDING", "待付款");
        statusMap.put("PAID", "已付款");
        statusMap.put("SHIPPED", "已发货");
        statusMap.put("COMPLETED", "已完成");
        statusMap.put("CANCELLED", "已取消");
        return statusMap.getOrDefault(status, "未知");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        System.out.println("=== 开始创建订单 ===");
        System.out.println("1. 用户ID: " + userId);
        System.out.println("2. 订单DTO: " + createOrderDTO);
        System.out.println("3. 订单DTO是否为null: " + (createOrderDTO == null));
        
        if (createOrderDTO != null) {
            System.out.println("4. items字段: " + createOrderDTO.getItems());
            System.out.println("5. items是否为null: " + (createOrderDTO.getItems() == null));
            if (createOrderDTO.getItems() != null) {
                System.out.println("6. items大小: " + createOrderDTO.getItems().size());
                System.out.println("7. items是否为空: " + createOrderDTO.getItems().isEmpty());
            }
        }
        
        // 验证收货信息
        if (createOrderDTO.getReceiverName() == null || createOrderDTO.getReceiverName().isEmpty()) {
            System.out.println("ERROR: 收货人姓名为空");
            throw new RuntimeException("请填写收货人姓名");
        }
        if (createOrderDTO.getReceiverPhone() == null || createOrderDTO.getReceiverPhone().isEmpty()) {
            System.out.println("ERROR: 收货人电话为空");
            throw new RuntimeException("请填写收货人电话");
        }
        if (createOrderDTO.getReceiverAddress() == null || createOrderDTO.getReceiverAddress().isEmpty()) {
            System.out.println("ERROR: 收货地址为空");
            throw new RuntimeException("请填写收货地址");
        }
        
        // 验证订单商品列表
        if (createOrderDTO.getItems() == null || createOrderDTO.getItems().isEmpty()) {
            System.out.println("ERROR: 订单商品信息不完整");
            System.out.println("   - items == null: " + (createOrderDTO.getItems() == null));
            System.out.println("   - items.isEmpty(): " + (createOrderDTO.getItems() != null && createOrderDTO.getItems().isEmpty()));
            throw new RuntimeException("订单商品信息不完整");
        }
        
        System.out.println("8. 验证通过，开始处理订单商品...");
        
        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> cartItemIdsToDelete = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPostage = BigDecimal.ZERO;
        
        // 处理订单商品
        for (CreateOrderDTO.OrderItemDTO itemDTO : createOrderDTO.getItems()) {
            // 验证商品ID和数量
            if (itemDTO.getCommodityId() == null) {
                throw new RuntimeException("商品ID不能为空");
            }
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new RuntimeException("商品数量必须大于0");
            }
            
            // 查询商品信息
            CommodityInfo commodity = commodityMapper.selectById(itemDTO.getCommodityId());
            if (commodity == null) {
                throw new RuntimeException("商品不存在");
            }
            
            if (commodity.getIsValid() == null || !commodity.getIsValid()) {
                throw new RuntimeException("商品已下架：" + commodity.getName());
            }
            
            // 创建订单商品项
            OrderItem orderItem = new OrderItem();
            orderItem.setCommodityId(commodity.getId());
            orderItem.setCommodityName(commodity.getName());
            orderItem.setCommodityPic(commodity.getMainPicUrl());
            orderItem.setCommodityPrice(commodity.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(commodity.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            
            // 累加邮费（每个商品的邮费）
            if (commodity.getPostage() != null) {
                totalPostage = totalPostage.add(commodity.getPostage());
            }
            
            // 如果有购物车项ID，记录下来用于后续删除
            if (itemDTO.getCartItemId() != null) {
                cartItemIdsToDelete.add(itemDTO.getCartItemId());
            }
        }
        
        if (orderItems.isEmpty()) {
            throw new RuntimeException("订单商品不能为空");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderSn(generateOrderSn(userId));
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPostage(totalPostage);
        order.setPayAmount(totalAmount.add(totalPostage));
        order.setStatus("PENDING");
        order.setReceiverName(createOrderDTO.getReceiverName());
        order.setReceiverPhone(createOrderDTO.getReceiverPhone());
        order.setReceiverAddress(createOrderDTO.getReceiverAddress());
        order.setRemark(createOrderDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        orderMapper.insert(order);
        
        // 保存订单商品明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        }
        
        // 删除购物车中的商品（仅当从购物车下单时）
        for (Long cartItemId : cartItemIdsToDelete) {
            CartItem cartItem = cartMapper.selectById(cartItemId);
            // 验证权限后再删除
            if (cartItem != null && cartItem.getUserId().equals(userId)) {
                cartMapper.deleteById(cartItemId);
            }
        }
        
        // 返回订单详情
        return getOrderDetail(order.getId(), userId);
    }
    
    @Override
    public List<OrderVO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderMapper.selectOrdersByUserId(userId);
        
        return orders.stream().map(order -> {
            OrderVO orderVO = convertToOrderVO(order);
            
            // 查询订单商品明细
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            List<OrderItemVO> itemVOs = items.stream().map(this::convertToOrderItemVO).collect(Collectors.toList());
            orderVO.setItems(itemVOs);
            
            return orderVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<OrderVO> getOrdersByUserIdAndStatus(Long userId, String status) {
        List<Order> orders = orderMapper.selectOrdersByUserId(userId);
        
        // 根据状态过滤
        if (status != null && !status.isEmpty() && !"ALL".equals(status)) {
            orders = orders.stream()
                    .filter(order -> status.equals(order.getStatus()))
                    .collect(Collectors.toList());
        }
        
        return orders.stream().map(order -> {
            OrderVO orderVO = convertToOrderVO(order);
            
            // 查询订单商品明细
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            List<OrderItemVO> itemVOs = items.stream().map(this::convertToOrderItemVO).collect(Collectors.toList());
            orderVO.setItems(itemVOs);
            
            return orderVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限查看该订单");
        }
        
        OrderVO orderVO = convertToOrderVO(order);
        
        // 查询订单商品明细
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        List<OrderItemVO> itemVOs = items.stream().map(this::convertToOrderItemVO).collect(Collectors.toList());
        orderVO.setItems(itemVOs);
        
        return orderVO;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        
        // 只有待付款状态的订单可以取消
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("该订单状态不允许取消");
        }
        
        // 查询订单商品明细，恢复库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            // 恢复库存
            int result = commodityMapper.increaseStock(item.getCommodityId(), item.getQuantity());
            if (result == 0) {
                throw new RuntimeException("恢复库存失败：" + item.getCommodityName());
            }
        }
        
        order.setStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        
        // 只有已取消或已完成的订单可以删除
        if (!"CANCELLED".equals(order.getStatus()) && !"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("该订单状态不允许删除");
        }
        
        // 删除订单（会级联删除订单商品明细）
        return orderMapper.deleteById(orderId) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmReceipt(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        
        // 只有运输中状态的订单可以确认收货
        if (!"SHIPPED".equals(order.getStatus())) {
            throw new RuntimeException("该订单状态不允许确认收货");
        }
        
        // 查询订单商品明细，增加销量
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            // 增加销量
            int result = commodityMapper.increaseSold(item.getCommodityId(), item.getQuantity());
            if (result == 0) {
                throw new RuntimeException("更新销量失败：" + item.getCommodityName());
            }
        }
        
        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(Long orderId, Long userId, String paymentMethod, String payPassword) {
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        
        // 只有待付款状态的订单可以支付
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("该订单状态不允许支付");
        }
        
        // 验证支付方式
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new RuntimeException("请选择支付方式");
        }
        
        // 创建支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setOrderId(orderId);
        paymentRecord.setUserId(userId);
        paymentRecord.setPaymentMethod(paymentMethod);
        paymentRecord.setAmount(order.getPayAmount());
        paymentRecord.setCreateTime(LocalDateTime.now());
        
        // 根据支付方式处理
        if ("WALLET".equals(paymentMethod)) {
            // 钱包支付
            if (payPassword == null || payPassword.isEmpty()) {
                paymentRecord.setStatus("FAILED");
                paymentRecordMapper.insert(paymentRecord);
                throw new RuntimeException("请输入支付密码");
            }
            
            // 查询钱包
            UserWallet wallet = walletMapper.selectByUserId(userId);
            if (wallet == null) {
                paymentRecord.setStatus("FAILED");
                paymentRecordMapper.insert(paymentRecord);
                throw new RuntimeException("钱包不存在，请先设置支付密码");
            }
            
            // 检查钱包是否锁定
            if (wallet.getIsLocked() != null && wallet.getIsLocked()) {
                paymentRecord.setStatus("FAILED");
                paymentRecordMapper.insert(paymentRecord);
                throw new RuntimeException("钱包已锁定，请联系客服");
            }
            
            // 验证支付密码
            if (wallet.getPayPassword() == null || wallet.getPayPassword().isEmpty()) {
                paymentRecord.setStatus("FAILED");
                paymentRecordMapper.insert(paymentRecord);
                throw new RuntimeException("请先设置支付密码");
            }
            
            if (!BCryptUtil.checkPassword(payPassword, wallet.getPayPassword())) {
                // 密码错误，增加错误次数
                wallet.setErrorCount(wallet.getErrorCount() + 1);
                
                if (wallet.getErrorCount() >= 3) {
                    // 锁定钱包
                    wallet.setIsLocked(true);
                    wallet.setLockTime(LocalDateTime.now());
                    walletMapper.updateById(wallet);
                    
                    paymentRecord.setStatus("FAILED");
                    paymentRecordMapper.insert(paymentRecord);
                    throw new RuntimeException("支付密码错误次数过多，钱包已锁定");
                } else {
                    walletMapper.updateById(wallet);
                    paymentRecord.setStatus("FAILED");
                    paymentRecordMapper.insert(paymentRecord);
                    throw new RuntimeException("支付密码错误，还可以尝试 " + (3 - wallet.getErrorCount()) + " 次");
                }
            }
            
            // 检查余额
            if (wallet.getBalance().compareTo(order.getPayAmount()) < 0) {
                paymentRecord.setStatus("FAILED");
                paymentRecordMapper.insert(paymentRecord);
                
                // 返回详细的余额不足信息
                String errorMsg = String.format("余额不足，当前余额：¥%.2f，需要支付：¥%.2f，还需充值：¥%.2f", 
                    wallet.getBalance(), 
                    order.getPayAmount(),
                    order.getPayAmount().subtract(wallet.getBalance()));
                throw new RuntimeException(errorMsg);
            }
            
            // 扣除余额
            wallet.setBalance(wallet.getBalance().subtract(order.getPayAmount()));
            wallet.setErrorCount(0); // 重置错误次数
            wallet.setUpdateTime(LocalDateTime.now());
            walletMapper.updateById(wallet);
            
            // 支付成功
            paymentRecord.setStatus("SUCCESS");
            paymentRecord.setPayTime(LocalDateTime.now());
            paymentRecord.setTransactionNo(generateTransactionNo());
            paymentRecordMapper.insert(paymentRecord);
            
        } else if ("WECHAT".equals(paymentMethod) || "ALIPAY".equals(paymentMethod)) {
            // 微信和支付宝暂不支持
            paymentRecord.setStatus("FAILED");
            paymentRecordMapper.insert(paymentRecord);
            throw new RuntimeException("暂不支持该支付方式");
        } else {
            paymentRecord.setStatus("FAILED");
            paymentRecordMapper.insert(paymentRecord);
            throw new RuntimeException("不支持的支付方式");
        }
        
        // 更新订单状态
        order.setStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 支付成功后，扣减商品库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            int result = commodityMapper.decreaseStock(item.getCommodityId(), item.getQuantity());
            if (result == 0) {
                // 库存不足，回滚事务
                throw new RuntimeException("商品库存不足：" + item.getCommodityName());
            }
        }
        
        return true;
    }
    
    /**
     * 生成交易流水号
     */
    private String generateTransactionNo() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

    
    /**
     * 转换为OrderVO
     */
    private OrderVO convertToOrderVO(Order order) {
        return OrderVO.builder()
                .id(order.getId())
                .orderSn(order.getOrderSn())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .postage(order.getPostage())
                .payAmount(order.getPayAmount())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .statusText(getStatusText(order.getStatus()))
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .payTime(order.getPayTime())
                .shipTime(order.getShipTime())
                .completeTime(order.getCompleteTime())
                .build();
    }
    
    /**
     * 转换为OrderItemVO
     */
    private OrderItemVO convertToOrderItemVO(OrderItem item) {
        return OrderItemVO.builder()
                .id(item.getId())
                .orderId(item.getOrderId())
                .commodityId(item.getCommodityId())
                .commodityName(item.getCommodityName())
                .commodityPic(item.getCommodityPic())
                .commodityPrice(item.getCommodityPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
