package com.yellow.petshop.service;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.home.CommodityDetailVO;
import com.yellow.petshop.model.home.CommodityInfo;

import java.util.List;

/**
 * 商品服务接口
 */
public interface GoodService {
    /**
     * 获取所有商品列表
     * @return 商品列表
     */
    List<CommodityInfo> getAllGoods();

    /**
     * 分页获取商品列表
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<CommodityInfo> getGoodsByPage(Long current, Long size);

    /**
     * 根据ID获取商品详情（包含图片列表）
     * @param id 商品ID
     * @return 商品详情
     */
    CommodityDetailVO getGoodDetail(Long id);
}
