package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.wallet.WalletTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钱包交易记录Mapper
 */
@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {
}
