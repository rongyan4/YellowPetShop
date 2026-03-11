package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.logistics.Logistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物流信息Mapper
 */
@Mapper
public interface LogisticsMapper extends BaseMapper<Logistics> {
}
