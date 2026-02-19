package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.wallet.UserWallet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户钱包Mapper
 */
public interface UserWalletMapper extends BaseMapper<UserWallet> {
    
    /**
     * 根据用户ID查询钱包
     */
    @Select("SELECT * FROM user_wallet WHERE user_id = #{userId}")
    UserWallet selectByUserId(@Param("userId") Long userId);
}
