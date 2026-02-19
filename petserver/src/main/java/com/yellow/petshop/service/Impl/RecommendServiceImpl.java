package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    private CommodityMapper commodityMapper;
    public List<CommodityInfo> getRecommendCommodityList(){
        return commodityMapper.selectList(null);
    }
}
