package com.yellow.petshop.service;

import com.yellow.petshop.model.browse.BrowseHistoryVO;

import java.util.List;

public interface BrowseHistoryService {
    
    /**
     * 添加浏览记录
     */
    boolean addBrowseHistory(Long userId, Long commodityId);
    
    /**
     * 获取用户浏览记录
     */
    List<BrowseHistoryVO> getBrowseHistoryList(Long userId, int limit);
    
    /**
     * 清空浏览记录
     */
    boolean clearBrowseHistory(Long userId);
}
