package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.logistics.OrderLogistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单物流映射Mapper
 */
@Mapper
public interface OrderLogisticsMapper extends BaseMapper<OrderLogistics> {
}
