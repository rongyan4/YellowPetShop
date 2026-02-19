package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.search.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 搜索历史Mapper
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
    
    /**
     * 获取用户最近的搜索记录（去重）
     * 使用子查询先排序再去重，确保获取最新的搜索记录
     */
    @Select("SELECT keyword FROM (SELECT keyword, MAX(search_time) as latest_time FROM search_history WHERE user_id = #{userId} GROUP BY keyword ORDER BY latest_time DESC LIMIT #{limit}) AS recent_searches")
    List<String> getRecentSearchKeywords(Long userId, int limit);
}
