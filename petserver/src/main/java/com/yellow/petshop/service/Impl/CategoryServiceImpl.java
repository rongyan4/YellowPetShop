package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.CategoryMapper;
import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.home.Category;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private CommodityMapper commodityMapper;
    
    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllOrderBySort();
    }
    
    /**
     * 获取分类及其商品预览（每个分类最多5个商品）
     * @return 分类及商品预览Map
     */
    @Override
    public Map<Category, List<CommodityInfo>> getCategoriesWithPreview() {
        List<Category> categories = categoryMapper.selectAllOrderBySort();
        Map<Category, List<CommodityInfo>> result = new LinkedHashMap<>();
        
        for (Category category : categories) {
            // 每个分类获取5个商品用于预览
            List<CommodityInfo> goods = commodityMapper.selectByCategoryLimit(category.getId(), 5);
            result.put(category, goods);
        }
        
        return result;
    }
    
    /**
     * 按分类分页获取商品列表
     * @param categoryId 分类ID
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<CommodityInfo> getGoodsByCategoryPage(Long categoryId, Long current, Long size) {
        // 计算偏移量
        Long offset = (current - 1) * size;
        
        // 查询当前页的数据
        List<CommodityInfo> records = commodityMapper.selectByCategoryPage(categoryId, offset, size);
        
        // 查询总记录数
        Long total = commodityMapper.selectCountByCategory(categoryId);
        
        // 计算总页数
        Long pages = (total + size - 1) / size;
        
        // 判断是否有下一页
        Boolean hasNext = current < pages;
        
        // 封装返回结果
        return PageResult.<CommodityInfo>builder()
                .current(current)
                .size(size)
                .total(total)
                .pages(pages)
                .records(records)
                .hasNext(hasNext)
                .build();
    }
}
