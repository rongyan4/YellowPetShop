package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.merchant.MerchantLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家登录日志Mapper接口
 */
@Mapper
public interface MerchantLoginLogMapper extends BaseMapper<MerchantLoginLog> {
}
