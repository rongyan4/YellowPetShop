package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.order.Order;
import com.yellow.petshop.model.order.OrderVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单Mapper
 */
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 查询用户订单列表（不含商品明细）
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> selectOrdersByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单号查询订单
     * @param orderSn 订单号
     * @return 订单
     */
    @Select("SELECT * FROM orders WHERE order_sn = #{orderSn}")
    Order selectByOrderSn(@Param("orderSn") String orderSn);
}
