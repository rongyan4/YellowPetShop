package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.browse.BrowseHistoryVO;
import com.yellow.petshop.model.browse.UserBrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrowseHistoryMapper extends BaseMapper<UserBrowseHistory> {
    
    @Select("SELECT h.id, h.commodity_id, c.name, c.price, c.main_pic_url, c.sold, h.browse_time " +
            "FROM user_browse_history h " +
            "LEFT JOIN commodity c ON h.commodity_id = c.id " +
            "WHERE h.user_id = #{userId} " +
            "ORDER BY h.browse_time DESC " +
            "LIMIT #{limit}")
    List<BrowseHistoryVO> getBrowseHistoryByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
