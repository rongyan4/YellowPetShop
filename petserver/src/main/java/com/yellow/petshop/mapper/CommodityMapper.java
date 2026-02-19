package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.home.CommodityInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommodityMapper extends BaseMapper<CommodityInfo> {
    
    /**
     * 分页查询商品列表
     * @param offset 偏移量（从第几条开始）
     * @param size 每页大小
     * @return 商品列表
     */
    @Select("SELECT * FROM commodity ORDER BY id DESC LIMIT #{offset}, #{size}")
    List<CommodityInfo> selectByPage(@Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 查询商品总数
     * @return 总记录数
     */
    @Select("SELECT COUNT(*) FROM commodity")
    Long selectTotalCount();
    
    /**
     * 按分类分页查询商品列表
     * @param categoryId 分类ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 商品列表
     */
    @Select("SELECT * FROM commodity WHERE category_id = #{categoryId} ORDER BY id DESC LIMIT #{offset}, #{size}")
    List<CommodityInfo> selectByCategoryPage(@Param("categoryId") Long categoryId, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 查询指定分类的商品总数
     * @param categoryId 分类ID
     * @return 总记录数
     */
    @Select("SELECT COUNT(*) FROM commodity WHERE category_id = #{categoryId}")
    Long selectCountByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 按分类查询指定数量的商品（用于分类预览）
     * @param categoryId 分类ID
     * @param limit 数量限制
     * @return 商品列表
     */
    @Select("SELECT * FROM commodity WHERE category_id = #{categoryId} ORDER BY id DESC LIMIT #{limit}")
    List<CommodityInfo> selectByCategoryLimit(@Param("categoryId") Long categoryId, @Param("limit") int limit);
}
