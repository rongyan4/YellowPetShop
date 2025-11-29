package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.Commodity;
import com.yellow.petshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    /**
     * 轮播图接口
     * 访问路径: /api/home/recommend
     *
     * @return 轮播图图片路径数组
     */
    @GetMapping("/recommend")
    public Result<List<Commodity>> getRecommendCommodityList(){
            List<Commodity> recommendCommodityList = recommendService.getRecommendCommodityList();
            return Result.success(recommendCommodityList);
    }
}