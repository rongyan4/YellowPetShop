package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.merchant.MerchantOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家操作日志Mapper接口
 */
@Mapper
public interface MerchantOperationLogMapper extends BaseMapper<MerchantOperationLog> {
}
