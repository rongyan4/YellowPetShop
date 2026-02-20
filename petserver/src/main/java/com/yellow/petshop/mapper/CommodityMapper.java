package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.home.CommodityInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommodityMapper extends BaseMapper<CommodityInfo> {
    
    /**
     * 分页查询商品列表（仅查询已上架商品）
     * @param offset 偏移量（从第几条开始）
     * @param size 每页大小
     * @return 商品列表
     */
    @Select("SELECT * FROM commodity WHERE is_valid = 1 ORDER BY id DESC LIMIT #{offset}, #{size}")
    List<CommodityInfo> selectByPage(@Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 查询商品总数（仅统计已上架商品）
     * @return 总记录数
     */
    @Select("SELECT COUNT(*) FROM commodity WHERE is_valid = 1")
    Long selectTotalCount();
    
    /**
     * 按分类分页查询商品列表（仅查询已上架商品）
     * @param categoryId 分类ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 商品列表
     */
    @Select("SELECT * FROM commodity WHERE category_id = #{categoryId} AND is_valid = 1 ORDER BY id DESC LIMIT #{offset}, #{size}")
    List<CommodityInfo> selectByCategoryPage(@Param("categoryId") Long categoryId, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 查询指定分类的商品总数（仅统计已上架商品）
     * @param categoryId 分类ID
     * @return 总记录数
     */
    @Select("SELECT COUNT(*) FROM commodity WHERE category_id = #{categoryId} AND is_valid = 1")
    Long selectCountByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 按分类查询指定数量的商品（用于分类预览，仅查询已上架商品）
     * @param categoryId 分类ID
     * @param limit 数量限制
     * @return 商品列表
     */
    @Select("SELECT * FROM commodity WHERE category_id = #{categoryId} AND is_valid = 1 ORDER BY id DESC LIMIT #{limit}")
    List<CommodityInfo> selectByCategoryLimit(@Param("categoryId") Long categoryId, @Param("limit") int limit);
    
    /**
     * 将指定分类下的所有商品的分类ID设置为NULL
     * @param categoryId 分类ID
     */
    @Update("UPDATE commodity SET category_id = NULL WHERE category_id = #{categoryId}")
    void updateCategoryToNull(@Param("categoryId") Long categoryId);
    
    /**
     * 减少商品库存
     * @param commodityId 商品ID
     * @param quantity 减少数量
     * @return 影响行数
     */
    @Update("UPDATE commodity SET stock = stock - #{quantity} WHERE id = #{commodityId} AND stock >= #{quantity}")
    int decreaseStock(@Param("commodityId") Long commodityId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品库存
     * @param commodityId 商品ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    @Update("UPDATE commodity SET stock = stock + #{quantity} WHERE id = #{commodityId}")
    int increaseStock(@Param("commodityId") Long commodityId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品销量
     * @param commodityId 商品ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    @Update("UPDATE commodity SET sold = sold + #{quantity} WHERE id = #{commodityId}")
    int increaseSold(@Param("commodityId") Long commodityId, @Param("quantity") Integer quantity);
}
