package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.wallet.WalletTransaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 钱包交易记录Mapper
 */
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {

    /**
     * 根据用户ID查询交易记录
     */
    @Select("SELECT * FROM wallet_transaction WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<WalletTransaction> selectByUserId(@Param("userId") Long userId);
}
