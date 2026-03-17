package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.favorite.FavoriteVO;
import com.yellow.petshop.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController extends BaseController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<String> addFavorite(HttpServletRequest request,
                                      @RequestParam Long commodityId) {
        Long userId = getUserId(request);
        boolean success = favoriteService.addFavorite(userId, commodityId);
        return success ? Result.success("收藏成功") : Result.error("已收藏该商品");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result<String> removeFavorite(HttpServletRequest request,
                                         @RequestParam Long commodityId) {
        Long userId = getUserId(request);
        boolean success = favoriteService.removeFavorite(userId, commodityId);
        return success ? Result.success("取消收藏成功") : Result.error("取消收藏失败");
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(HttpServletRequest request,
                                         @RequestParam Long commodityId) {
        Long userId = getUserId(request);
        boolean isFavorite = favoriteService.isFavorite(userId, commodityId);
        return Result.success(isFavorite);
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteVO>> getFavoriteList(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<FavoriteVO> list = favoriteService.getFavoriteList(userId);
        return Result.success(list);
    }
}
