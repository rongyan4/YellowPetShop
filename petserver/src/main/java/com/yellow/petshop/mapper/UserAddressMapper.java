package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.address.UserAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户地址Mapper
 */
public interface UserAddressMapper extends BaseMapper<UserAddress> {
    
    /**
     * 查询用户所有地址
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} ORDER BY is_default DESC, create_time DESC")
    List<UserAddress> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户默认地址
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    UserAddress selectDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 取消用户所有默认地址
     */
    @Update("UPDATE user_address SET is_default = 0 WHERE user_id = #{userId}")
    int cancelAllDefault(@Param("userId") Long userId);
}
