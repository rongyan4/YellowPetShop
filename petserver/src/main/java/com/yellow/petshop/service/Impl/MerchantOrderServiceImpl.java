package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.*;
import com.yellow.petshop.model.merchant.DashboardVO;
import com.yellow.petshop.model.merchant.OrderDetailVO;
import com.yellow.petshop.model.merchant.ShipOrderDTO;
import com.yellow.petshop.model.merchant.UpdateOrderPriceDTO;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.order.Order;
import com.yellow.petshop.model.order.OrderItem;
import com.yellow.petshop.model.order.OrderItemVO;
import com.yellow.petshop.model.order.OrderVO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.service.MerchantOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商家订单服务实现
 */
@Service
public class MerchantOrderServiceImpl implements MerchantOrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private CommodityMapper commodityMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public DashboardVO getDashboard() {
        DashboardVO dashboard = new DashboardVO();
        
        // 今日开始时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        
        // 今日订单数和销售额
        QueryWrapper<Order> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("create_time", todayStart);
        List<Order> todayOrders = orderMapper.selectList(todayWrapper);
        dashboard.setTodayOrderCount(todayOrders.size());
        
        BigDecimal todaySales = todayOrders.stream()
                .filter(o -> "paid".equals(o.getStatus()) || "shipped".equals(o.getStatus()) || "completed".equals(o.getStatus()))
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTodaySales(todaySales);
        
        // 待处理订单数（待支付）
        QueryWrapper<Order> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "pending");
        dashboard.setPendingOrderCount(Math.toIntExact(orderMapper.selectCount(pendingWrapper)));
        
        // 待发货订单数
        QueryWrapper<Order> toShipWrapper = new QueryWrapper<>();
        toShipWrapper.eq("status", "paid");
        dashboard.setToShipOrderCount(Math.toIntExact(orderMapper.selectCount(toShipWrapper)));
        
        // 商品统计
        Long totalProducts = commodityMapper.selectTotalCount();
        dashboard.setTotalProductCount(totalProducts.intValue());
        
        QueryWrapper<com.yellow.petshop.model.home.CommodityInfo> onSaleWrapper = new QueryWrapper<>();
        onSaleWrapper.eq("is_valid", true);
        dashboard.setOnSaleProductCount(Math.toIntExact(commodityMapper.selectCount(onSaleWrapper)));
        
        QueryWrapper<com.yellow.petshop.model.home.CommodityInfo> offSaleWrapper = new QueryWrapper<>();
        offSaleWrapper.eq("is_valid", false);
        dashboard.setOffSaleProductCount(Math.toIntExact(commodityMapper.selectCount(offSaleWrapper)));
        
        // 总销售额和总订单数
        List<Order> allOrders = orderMapper.selectList(null);
        dashboard.setTotalOrderCount(allOrders.size());
        
        BigDecimal totalSales = allOrders.stream()
                .filter(o -> "paid".equals(o.getStatus()) || "shipped".equals(o.getStatus()) || "completed".equals(o.getStatus()))
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalSales(totalSales);
        
        // 待评价订单数
        QueryWrapper<Order> toCommentWrapper = new QueryWrapper<>();
        toCommentWrapper.eq("status", "completed");
        dashboard.setToCommentOrderCount(Math.toIntExact(orderMapper.selectCount(toCommentWrapper)));
        
        // 评论统计
        QueryWrapper<com.yellow.petshop.model.comment.Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("status", "normal");
        dashboard.setTotalCommentCount(Math.toIntExact(commentMapper.selectCount(commentWrapper)));
        
        QueryWrapper<com.yellow.petshop.model.comment.Comment> toReplyWrapper = new QueryWrapper<>();
        toReplyWrapper.eq("status", "normal");
        toReplyWrapper.isNull("merchant_reply");
        dashboard.setToReplyCommentCount(Math.toIntExact(commentMapper.selectCount(toReplyWrapper)));
        
        return dashboard;
    }

    @Override
    public PageResult<OrderVO> getOrderList(String status, Integer page, Integer size) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        
        // 计算偏移量
        long offset = (long) (page - 1) * size;
        
        // 查询总数
        Long total = orderMapper.selectCount(wrapper);
        
        // 分页查询
        wrapper.last("LIMIT " + offset + ", " + size);
        List<Order> orders = orderMapper.selectList(wrapper);
        
        // 转换为VO
        List<OrderVO> orderVOs = orders.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            
            // 查询订单项
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            
            List<OrderItemVO> itemVOs = items.stream().map(item -> {
                OrderItemVO itemVO = new OrderItemVO();
                BeanUtils.copyProperties(item, itemVO);
                return itemVO;
            }).collect(Collectors.toList());
            
            vo.setItems(itemVOs);
            return vo;
        }).collect(Collectors.toList());
        
        PageResult<OrderVO> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(orderVOs);
        result.setCurrent(page.longValue());
        result.setSize(size.longValue());
        result.setPages((total + size - 1) / size);
        
        return result;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        
        // 查询用户信息
        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            vo.setUserPhone(user.getPhone());
        }
        
        // 查询订单项
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        
        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        
        vo.setOrderItems(itemVOs);
        
        return vo;
    }

    @Override
    @Transactional
    public void updateOrderPrice(UpdateOrderPriceDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("只能修改待支付订单的价格");
        }
        
        // 保存原始金额
        if (order.getOriginalAmount() == null) {
            order.setOriginalAmount(order.getTotalAmount());
        }
        
        // 更新价格
        order.setTotalAmount(dto.getNewTotalAmount());
        order.setPostage(dto.getNewPostage());
        order.setPayAmount(dto.getNewTotalAmount().add(dto.getNewPostage()));
        order.setPriceModified(true);
        order.setUpdateTime(LocalDateTime.now());
        
        if (dto.getReason() != null && !dto.getReason().isEmpty()) {
            String remark = order.getRemark() != null ? order.getRemark() : "";
            remark += "\n【改价说明】" + dto.getReason();
            order.setRemark(remark);
        }
        
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void shipOrder(ShipOrderDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("只能对已付款订单进行发货");
        }
        
        // 更新订单状态
        order.setStatus("shipped");
        order.setShippingStatus("shipped");
        order.setShippingCompany(dto.getShippingCompany());
        order.setTrackingNo(dto.getTrackingNo());
        order.setShippingTime(LocalDateTime.now());
        order.setShipTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        if (dto.getRemark() != null && !dto.getRemark().isEmpty()) {
            String remark = order.getRemark() != null ? order.getRemark() : "";
            remark += "\n【发货备注】" + dto.getRemark();
            order.setRemark(remark);
        }
        
        orderMapper.updateById(order);
        
        // 更新商品销量
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", dto.getOrderId());
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        
        for (OrderItem item : items) {
            com.yellow.petshop.model.home.CommodityInfo commodity = commodityMapper.selectById(item.getCommodityId());
            if (commodity != null) {
                int newSold = (commodity.getSold() != null ? commodity.getSold() : 0) + item.getQuantity();
                commodity.setSold(newSold);
                commodityMapper.updateById(commodity);
            }
        }
    }
}
