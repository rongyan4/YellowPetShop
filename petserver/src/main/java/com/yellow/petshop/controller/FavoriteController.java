package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.favorite.FavoriteVO;
import com.yellow.petshop.service.FavoriteService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestHeader("Authorization") String token,
                                      @RequestParam Long commodityId) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        boolean success = favoriteService.addFavorite(userId, commodityId);
        
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("已收藏该商品");
        }
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result<String> removeFavorite(@RequestHeader("Authorization") String token,
                                         @RequestParam Long commodityId) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        boolean success = favoriteService.removeFavorite(userId, commodityId);
        
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestHeader("Authorization") String token,
                                         @RequestParam Long commodityId) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        boolean isFavorite = favoriteService.isFavorite(userId, commodityId);
        
        return Result.success(isFavorite);
    }
    
    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteVO>> getFavoriteList(@RequestHeader("Authorization") String token) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        List<FavoriteVO> list = favoriteService.getFavoriteList(userId);
        
        return Result.success(list);
    }
}
