package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.favorite.FavoriteVO;
import com.yellow.petshop.model.favorite.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<UserFavorite> {
    
    @Select("SELECT f.id, f.commodity_id, c.name, c.price, c.main_pic_url, c.sold, f.create_time " +
            "FROM user_favorite f " +
            "LEFT JOIN commodity c ON f.commodity_id = c.id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.create_time DESC")
    List<FavoriteVO> getFavoritesByUserId(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND commodity_id = #{commodityId}")
    int checkFavorite(@Param("userId") Long userId, @Param("commodityId") Long commodityId);
}
