package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.service.SwipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class SwipeController {
    @Autowired
    private SwipeService homeService;
    /**
     * 轮播图接口
     * 访问路径: /api/home/swipe
     * @return 轮播图图片路径数组
     */
    @GetMapping("/swipe")
    public Result<List<String>> getSwipeImages() {
            List<String> swipeList = homeService.getSwipeList();
            return Result.success(swipeList);
    }
}