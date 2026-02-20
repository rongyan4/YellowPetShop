package com.yellow.petshop.service;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.home.Category;
import com.yellow.petshop.model.home.CommodityInfo;

import java.util.List;
import java.util.Map;

/**
 * 分类服务接口
 */
public interface CategoryService {
    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 获取分类及其商品预览（每个分类最多5个商品）
     * @return 分类及商品预览Map
     */
    Map<Category, List<CommodityInfo>> getCategoriesWithPreview();
    
    /**
     * 按分类分页获取商品列表
     * @param categoryId 分类ID
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<CommodityInfo> getGoodsByCategoryPage(Long categoryId, Long current, Long size);
    
    /**
     * 添加分类
     * @param category 分类信息
     */
    void addCategory(Category category);
    
    /**
     * 更新分类
     * @param category 分类信息
     */
    void updateCategory(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}
