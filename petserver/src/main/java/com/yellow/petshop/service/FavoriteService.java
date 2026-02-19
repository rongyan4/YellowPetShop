package com.yellow.petshop.service;

import com.yellow.petshop.model.favorite.FavoriteVO;

import java.util.List;

public interface FavoriteService {
    
    /**
     * 添加收藏
     */
    boolean addFavorite(Long userId, Long commodityId);
    
    /**
     * 取消收藏
     */
    boolean removeFavorite(Long userId, Long commodityId);
    
    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Long userId, Long commodityId);
    
    /**
     * 获取用户收藏列表
     */
    List<FavoriteVO> getFavoriteList(Long userId);
}
