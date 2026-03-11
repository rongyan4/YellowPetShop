package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.*;
import com.yellow.petshop.model.order.Order;
import com.yellow.petshop.model.order.OrderItem;
import com.yellow.petshop.model.order.OrderItemVO;
import com.yellow.petshop.model.order.OrderVO;
import com.yellow.petshop.model.user.User;
import com.yellow.petshop.model.wallet.UserWallet;
import com.yellow.petshop.service.MerchantMemberService;
import com.yellow.petshop.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商家会员管理服务实现类
 */
@Service
public class MerchantMemberServiceImpl implements MerchantMemberService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private WalletMapper walletMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    public Map<String, Object> getMemberList(int page, int pageSize, String keyword) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 只查询普通用户
        wrapper.eq("role", "user");
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like("username", kw)
                    .or().like("nickname", kw)
                    .or().like("phone", kw));
        }
        wrapper.orderByDesc("create_time");

        long offset = (long) (page - 1) * pageSize;
        Long total = userMapper.selectCount(wrapper);

        if (offset < 0) {
            offset = 0;
        }

        // 分页查询
        wrapper.last("LIMIT " + offset + ", " + pageSize);
        List<User> list = userMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total != null ? total : 0L);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }

    @Override
    public Map<String, Object> getMemberDetail(Long userId) {
        // 获取用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 获取钱包信息
        QueryWrapper<UserWallet> walletWrapper = new QueryWrapper<>();
        walletWrapper.eq("user_id", userId);
        UserWallet wallet = walletMapper.selectOne(walletWrapper);
        
        // 将用户信息和钱包信息合并
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("id", user.getId());
        memberData.put("username", user.getUsername());
        memberData.put("email", user.getEmail());
        memberData.put("nickname", user.getNickname());
        memberData.put("gender", user.getGender());
        memberData.put("avatar", user.getAvatar());
        memberData.put("status", user.getStatus());
        memberData.put("role", user.getRole());
        memberData.put("birthday", user.getBirthday());
        memberData.put("phone", user.getPhone());
        memberData.put("points", user.getPoints());
        memberData.put("createTime", user.getCreateTime());
        
        // 添加钱包信息到用户数据中
        if (wallet != null) {
            memberData.put("balance", wallet.getBalance());
            memberData.put("hasPayPassword", wallet.getPayPassword() != null && !wallet.getPayPassword().isEmpty());
        } else {
            memberData.put("balance", BigDecimal.ZERO);
            memberData.put("hasPayPassword", false);
        }
        
        return memberData;
    }
    
    @Override
    @Transactional
    public void updateMemberInfo(Long userId, Map<String, Object> data) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (data.containsKey("nickname")) {
            user.setNickname((String) data.get("nickname"));
        }
        if (data.containsKey("email")) {
            user.setEmail((String) data.get("email"));
        }
        if (data.containsKey("phone")) {
            user.setPhone((String) data.get("phone"));
        }
        if (data.containsKey("gender")) {
            user.setGender((String) data.get("gender"));
        }
        if (data.containsKey("birthday")) {
            user.setBirthday((String) data.get("birthday"));
        }
        
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void adjustMemberBalance(Long userId, Map<String, Object> data, Long merchantId) {
        // 获取或创建钱包
        QueryWrapper<UserWallet> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserWallet wallet = walletMapper.selectOne(wrapper);
        
        if (wallet == null) {
            wallet = new UserWallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCreateTime(LocalDateTime.now());
            walletMapper.insert(wallet);
        }
        
        // 获取调整金额
        Object amountObj = data.get("amount");
        BigDecimal amount;
        if (amountObj instanceof String) {
            amount = new BigDecimal((String) amountObj);
        } else if (amountObj instanceof Number) {
            amount = BigDecimal.valueOf(((Number) amountObj).doubleValue());
        } else {
            throw new RuntimeException("金额格式错误");
        }
        
        // 计算新余额
        BigDecimal newBalance = wallet.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("余额不足，无法扣减");
        }
        
        wallet.setBalance(newBalance);
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
        
        // 记录日志（可选）
        String remark = (String) data.get("remark");
        System.out.println("商家ID: " + merchantId + " 调整用户ID: " + userId + " 余额: " + amount + " 备注: " + remark);
    }
    
    @Override
    @Transactional
    public void resetMemberPayPassword(Long userId, String newPassword) {
        // 获取或创建钱包
        QueryWrapper<UserWallet> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserWallet wallet = walletMapper.selectOne(wrapper);
        
        if (wallet == null) {
            wallet = new UserWallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCreateTime(LocalDateTime.now());
        }
        
        // 加密密码
        String encryptedPassword = BCryptUtil.encrypt(newPassword);
        wallet.setPayPassword(encryptedPassword);
        wallet.setUpdateTime(LocalDateTime.now());
        
        if (wallet.getId() == null) {
            walletMapper.insert(wallet);
        } else {
            walletMapper.updateById(wallet);
        }
    }
    
    @Override
    public Map<String, Object> getMemberOrders(Long userId, int page, int pageSize) {
        // 查询订单列表
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        
        List<Order> allOrders = orderMapper.selectList(wrapper);
        int total = allOrders.size();
        
        // 分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Order> pageOrders = allOrders.subList(start, end);
        
        // 转换为VO
        List<OrderVO> orderVOList = pageOrders.stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setOrderSn(order.getOrderSn());
            vo.setUserId(order.getUserId());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPostage(order.getPostage());
            vo.setPayAmount(order.getPayAmount());
            vo.setStatus(order.getStatus());
            vo.setStatusText(getStatusText(order.getStatus()));
            vo.setCreateTime(order.getCreateTime());
            vo.setPayTime(order.getPayTime());
            vo.setPaymentMethod(order.getPaymentMethod());
            
            // 查询订单商品
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
            List<OrderItemVO> items = orderItems.stream().map(item -> {
                OrderItemVO itemVO = new OrderItemVO();
                itemVO.setId(item.getId());
                itemVO.setOrderId(item.getOrderId());
                itemVO.setCommodityId(item.getCommodityId());
                itemVO.setCommodityName(item.getCommodityName());
                itemVO.setCommodityPic(item.getCommodityPic());
                itemVO.setCommodityPrice(item.getCommodityPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setTotalPrice(item.getTotalPrice());
                return itemVO;
            }).collect(Collectors.toList());
            vo.setItems(items);
            
            return vo;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderVOList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int normalizedStatus = (status != null && status == 1) ? 1 : 0;
        user.setIsValid(normalizedStatus);
        user.setStatus(normalizedStatus == 1 ? "active" : "inactive");
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteMember(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 逻辑删除：禁用账号，避免物理删除影响关联数据
        user.setIsValid(0);
        user.setStatus("inactive");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private String getStatusText(String status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("PENDING", "待付款");
        statusMap.put("PAID", "已付款");
        statusMap.put("SHIPPED", "已发货");
        statusMap.put("COMPLETED", "已完成");
        statusMap.put("CANCELLED", "已取消");
        return statusMap.getOrDefault(status, "未知");
    }
}
