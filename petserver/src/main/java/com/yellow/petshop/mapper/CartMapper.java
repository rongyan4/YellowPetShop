package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.cart.CartItem;
import com.yellow.petshop.model.cart.CartItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车Mapper
 */
public interface CartMapper extends BaseMapper<CartItem> {
    
    /**
     * 查询用户购物车列表（包含商品信息）
     * @param userId 用户ID
     * @return 购物车列表
     */
    @Select("SELECT ci.id, ci.user_id, ci.commodity_id, ci.quantity, ci.checked, " +
            "c.name, c.price, c.unit, c.main_pic_url, c.msg, c.is_valid, c.postage " +
            "FROM cart_item ci " +
            "LEFT JOIN commodity c ON ci.commodity_id = c.id " +
            "WHERE ci.user_id = #{userId} " +
            "ORDER BY ci.create_time DESC")
    List<CartItemVO> selectCartByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户购物车中的指定商品
     * @param userId 用户ID
     * @param commodityId 商品ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND commodity_id = #{commodityId}")
    int deleteByUserIdAndCommodityId(@Param("userId") Long userId, @Param("commodityId") Long commodityId);
}
