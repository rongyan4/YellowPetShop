package com.yellow.petshop.service;

import com.yellow.petshop.model.home.CommodityInfo;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param userId 用户ID（可选，用于保存搜索历史）
     * @return 商品列表
     */
    List<CommodityInfo> searchGoods(String keyword, Long userId);
    
    /**
     * 获取用户搜索历史
     * @param userId 用户ID
     * @return 搜索关键词列表
     */
    List<String> getSearchHistory(Long userId);
    
    /**
     * 清除用户搜索历史
     * @param userId 用户ID
     */
    void clearSearchHistory(Long userId);
    
    /**
     * 获取热门搜索推荐
     * @return 热门关键词列表
     */
    List<String> getHotSearchKeywords();
}
