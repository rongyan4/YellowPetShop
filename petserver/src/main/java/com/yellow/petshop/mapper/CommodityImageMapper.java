package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.home.CommodityImage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品图片Mapper
 */
public interface CommodityImageMapper extends BaseMapper<CommodityImage> {
    
    /**
     * 根据商品ID查询图片列表
     * @param commodityId 商品ID
     * @return 图片列表
     */
    @Select("SELECT * FROM commodity_image WHERE commodity_id = #{commodityId} ORDER BY sort_order ASC")
    List<CommodityImage> selectByCommodityId(@Param("commodityId") Long commodityId);
}
