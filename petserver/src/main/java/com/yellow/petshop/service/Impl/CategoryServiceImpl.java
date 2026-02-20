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
    
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllOrderBySort();
    }
    
    @Override
    public Map<Category, List<CommodityInfo>> getCategoriesWithPreview() {
        // 使用 LinkedHashMap 保持顺序
        Map<Category, List<CommodityInfo>> result = new LinkedHashMap<>();
        
        // 获取所有分类
        List<Category> categories = categoryMapper.selectAllOrderBySort();
        
        // 为每个分类查询最多5个商品
        for (Category category : categories) {
            List<CommodityInfo> commodities = commodityMapper.selectByCategoryLimit(category.getId(), 5);
            result.put(category, commodities);
        }
        
        return result;
    }
    
    @Override
    public PageResult<CommodityInfo> getGoodsByCategoryPage(Long categoryId, Long current, Long size) {
        // 计算偏移量
        Long offset = (current - 1) * size;
        
        // 查询商品列表
        List<CommodityInfo> records = commodityMapper.selectByCategoryPage(categoryId, offset, size);
        
        // 查询总数
        Long total = commodityMapper.selectCountByCategory(categoryId);
        
        // 构建分页结果
        PageResult<CommodityInfo> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setCurrent(current);
        pageResult.setSize(size);
        
        return pageResult;
    }
    
    @Override
    public void addCategory(Category category) {
        categoryMapper.insert(category);
    }
    
    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }
    
    @Override
    public void deleteCategory(Long id) {
        // 删除分类前，将该分类下的商品的分类ID设置为NULL
        commodityMapper.updateCategoryToNull(id);
        // 删除分类
        categoryMapper.deleteById(id);
    }
}
