package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yellow.petshop.mapper.BrowseHistoryMapper;
import com.yellow.petshop.model.browse.BrowseHistoryVO;
import com.yellow.petshop.model.browse.UserBrowseHistory;
import com.yellow.petshop.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {
    
    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;
    
    @Override
    public boolean addBrowseHistory(Long userId, Long commodityId) {
        // 检查是否已存在该商品的浏览记录
        LambdaQueryWrapper<UserBrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBrowseHistory::getUserId, userId)
               .eq(UserBrowseHistory::getCommodityId, commodityId);
        
        UserBrowseHistory existing = browseHistoryMapper.selectOne(wrapper);
        
        if (existing != null) {
            // 更新浏览时间
            existing.setBrowseTime(LocalDateTime.now());
            return browseHistoryMapper.updateById(existing) > 0;
        } else {
            // 新增浏览记录
            UserBrowseHistory history = UserBrowseHistory.builder()
                    .userId(userId)
                    .commodityId(commodityId)
                    .browseTime(LocalDateTime.now())
                    .build();
            
            return browseHistoryMapper.insert(history) > 0;
        }
    }
    
    @Override
    public List<BrowseHistoryVO> getBrowseHistoryList(Long userId, int limit) {
        return browseHistoryMapper.getBrowseHistoryByUserId(userId, limit);
    }
    
    @Override
    public boolean clearBrowseHistory(Long userId) {
        LambdaQueryWrapper<UserBrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBrowseHistory::getUserId, userId);
        
        return browseHistoryMapper.delete(wrapper) > 0;
    }
}
