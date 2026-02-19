package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.CommodityDetailVO;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品控制器
 * 统一管理商品相关的所有接口
 */
@RestController
@RequestMapping("/api/goods")
public class GoodController {
    @Autowired
    private GoodService goodService;

    /**
     * 获取所有商品列表
     * 访问路径: GET /api/goods/all
     *
     * @return 商品列表
     */
    @GetMapping("/all")
    public Result<List<CommodityInfo>> getAllGoods(){
        List<CommodityInfo> goodsList = goodService.getAllGoods();
        return Result.success(goodsList);
    }

    /**
     * 分页获取商品列表
     * 访问路径: GET /api/goods/page?current=1&size=8
     *
     * @param current 当前页码（从1开始），默认为1
     * @param size 每页大小，默认为8
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<CommodityInfo>> getGoodsByPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "8") Long size) {
        PageResult<CommodityInfo> pageResult = goodService.getGoodsByPage(current, size);
        return Result.success(pageResult);
    }

    /**
     * 获取商品详情（包含图片列表、发货地、邮费等完整信息）
     * 访问路径: GET /api/goods/detail?id=商品ID
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/detail")
    public Result<CommodityDetailVO> getGoodDetail(@RequestParam Long id){
        CommodityDetailVO goodDetail = goodService.getGoodDetail(id);
        if (goodDetail != null) {
            return Result.success(goodDetail);
        } else {
            return Result.error("商品不存在");
        }
    }
}

