package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yellow.petshop.mapper.FavoriteMapper;
import com.yellow.petshop.model.favorite.FavoriteVO;
import com.yellow.petshop.model.favorite.UserFavorite;
import com.yellow.petshop.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Override
    public boolean addFavorite(Long userId, Long commodityId) {
        // 检查是否已收藏
        if (isFavorite(userId, commodityId)) {
            return false;
        }
        
        UserFavorite favorite = UserFavorite.builder()
                .userId(userId)
                .commodityId(commodityId)
                .createTime(LocalDateTime.now())
                .build();
        
        return favoriteMapper.insert(favorite) > 0;
    }
    
    @Override
    public boolean removeFavorite(Long userId, Long commodityId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getCommodityId, commodityId);
        
        return favoriteMapper.delete(wrapper) > 0;
    }
    
    @Override
    public boolean isFavorite(Long userId, Long commodityId) {
        return favoriteMapper.checkFavorite(userId, commodityId) > 0;
    }
    
    @Override
    public List<FavoriteVO> getFavoriteList(Long userId) {
        return favoriteMapper.getFavoritesByUserId(userId);
    }
}
