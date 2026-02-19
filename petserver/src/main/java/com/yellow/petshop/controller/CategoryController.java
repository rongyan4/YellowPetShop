package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.Category;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有分类列表
     * 访问路径: GET /api/category/all
     * @return 分类列表
     */
    @GetMapping("/all")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 获取分类及其商品预览
     * 访问路径: GET /api/category/preview
     * @return 分类及商品预览
     */
    @GetMapping("/preview")
    public Result<Map<Category, List<CommodityInfo>>> getCategoriesWithPreview() {
        Map<Category, List<CommodityInfo>> result = categoryService.getCategoriesWithPreview();
        return Result.success(result);
    }
    
    /**
     * 按分类分页获取商品列表
     * 访问路径: GET /api/category/goods?categoryId=1&current=1&size=10
     * @param categoryId 分类ID
     * @param current 当前页码（从1开始），默认为1
     * @param size 每页大小，默认为10
     * @return 分页结果
     */
    @GetMapping("/goods")
    public Result<PageResult<CommodityInfo>> getGoodsByCategoryPage(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<CommodityInfo> pageResult = categoryService.getGoodsByCategoryPage(categoryId, current, size);
        return Result.success(pageResult);
    }
}
