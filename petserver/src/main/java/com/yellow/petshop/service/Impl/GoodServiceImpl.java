package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.CommodityImageMapper;
import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.home.CommodityDetailVO;
import com.yellow.petshop.model.home.CommodityImage;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.GoodService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Service
public class GoodServiceImpl implements GoodService {
    @Autowired
    private CommodityMapper commodityMapper;
    
    @Autowired
    private CommodityImageMapper commodityImageMapper;

    /**
     * 获取所有商品列表（仅返回已上架商品）
     * @return 商品列表
     */
    @Override
    public List<CommodityInfo> getAllGoods(){
        // 使用 MyBatis-Plus 的条件构造器查询已上架商品
        return commodityMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CommodityInfo>()
                .eq("is_valid", 1)
        );
    }

    /**
     * 手动实现分页获取商品列表
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<CommodityInfo> getGoodsByPage(Long current, Long size) {
        // 计算偏移量：(当前页 - 1) * 每页大小
        Long offset = (current - 1) * size;
        
        // 查询当前页的数据
        List<CommodityInfo> records = commodityMapper.selectByPage(offset, size);
        
        // 查询总记录数
        Long total = commodityMapper.selectTotalCount();
        
        // 计算总页数：总记录数 / 每页大小，向上取整
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

    /**
     * 根据ID获取商品详情（包含图片列表）
     * 只返回已上架的商品
     * @param id 商品ID
     * @return 商品详情
     */
    @Override
    public CommodityDetailVO getGoodDetail(Long id){
        // 查询商品基本信息
        CommodityInfo commodityInfo = commodityMapper.selectById(id);
        if (commodityInfo == null) {
            return null;
        }
        
        // 检查商品是否上架
        if (commodityInfo.getIsValid() == null) {
            return null;
        }
        
        // 查询商品图片列表
        List<CommodityImage> imageList = commodityImageMapper.selectByCommodityId(id);
        List<String> images = imageList.stream()
                .map(CommodityImage::getImageUrl)
                .collect(Collectors.toList());
        
        // 如果没有图片，使用主图
        if (images.isEmpty() && commodityInfo.getMainPicUrl() != null) {
            images.add(commodityInfo.getMainPicUrl());
        }
        
        // 构建详情VO
        CommodityDetailVO detailVO = CommodityDetailVO.builder()
                .id(commodityInfo.getId())
                .name(commodityInfo.getName())
                .price(commodityInfo.getPrice())
                .unit(commodityInfo.getUnit())
                .sold(commodityInfo.getSold())
                .mainPicUrl(commodityInfo.getMainPicUrl())
                .msg(commodityInfo.getMsg())
                .detail(commodityInfo.getDetail())
                .isValid(commodityInfo.getIsValid())
                .shippingOrigin(commodityInfo.getShippingOrigin())
                .postage(commodityInfo.getPostage())
                .images(images)
                .build();
        
        return detailVO;
    }
}
