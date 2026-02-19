package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.mapper.SearchHistoryMapper;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.search.SearchHistory;
import com.yellow.petshop.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 搜索服务实现类
 */
@Service
public class SearchServiceImpl implements SearchService {
    
    @Autowired
    private CommodityMapper commodityMapper;
    
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    
    @Override
    public List<CommodityInfo> searchGoods(String keyword, Long userId) {
        // 保存搜索历史
        if (userId != null && keyword != null && !keyword.trim().isEmpty()) {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setKeyword(keyword.trim());
            history.setSearchTime(LocalDateTime.now());
            searchHistoryMapper.insert(history);
        }
        
        // 搜索商品（按商品名称或详情模糊查询）
        LambdaQueryWrapper<CommodityInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CommodityInfo::getName, keyword)
               .or()
               .like(CommodityInfo::getMsg, keyword);
        
        return commodityMapper.selectList(wrapper);
    }
    
    @Override
    public List<String> getSearchHistory(Long userId) {
        if (userId == null) {
            return Arrays.asList();
        }
        // 获取最近10条搜索记录
        return searchHistoryMapper.getRecentSearchKeywords(userId, 10);
    }
    
    @Override
    public void clearSearchHistory(Long userId) {
        if (userId != null) {
            LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SearchHistory::getUserId, userId);
            searchHistoryMapper.delete(wrapper);
        }
    }
    
    @Override
    public List<String> getHotSearchKeywords() {
        // 返回热门搜索关键词
        return Arrays.asList(
            "猫粮", 
            "狗粮", 
            "猫砂", 
            "宠物玩具", 
            "宠物零食",
            "猫咪用品",
            "狗狗用品",
            "宠物服装"
        );
    }
}
