package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.merchant.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家Mapper接口
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
}
