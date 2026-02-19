package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.SearchService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    /**
     * 搜索商品
     * 访问路径: GET /api/search?keyword=关键词
     */
    @GetMapping("/search")
    public Result<List<CommodityInfo>> searchGoods(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwtToken = token.substring(7);
                userId = JwtUtil.getUserIdFromToken(jwtToken);
            } catch (Exception e) {
                // 忽略token解析错误，允许未登录用户搜索
            }
        }
        
        List<CommodityInfo> result = searchService.searchGoods(keyword, userId);
        return Result.success(result);
    }
    
    /**
     * 获取搜索历史
     * 访问路径: GET /api/history_search
     */
    @GetMapping("/history_search")
    public Result<List<String>> getSearchHistory(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwtToken = token.substring(7);
                userId = JwtUtil.getUserIdFromToken(jwtToken);
            } catch (Exception e) {
                return Result.error("请先登录");
            }
        }
        
        List<String> history = searchService.getSearchHistory(userId);
        return Result.success(history);
    }
    
    /**
     * 清除搜索历史
     * 访问路径: DELETE /api/history_search
     */
    @DeleteMapping("/history_search")
    public Result<Void> clearSearchHistory(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwtToken = token.substring(7);
                userId = JwtUtil.getUserIdFromToken(jwtToken);
            } catch (Exception e) {
                return Result.error("请先登录");
            }
        }
        
        searchService.clearSearchHistory(userId);
        return Result.success(null);
    }
    
    /**
     * 获取热门搜索推荐
     * 访问路径: GET /api/guss_you_want
     */
    @GetMapping("/guss_you_want")
    public Result<List<String>> getHotSearchKeywords() {
        List<String> keywords = searchService.getHotSearchKeywords();
        return Result.success(keywords);
    }
}
