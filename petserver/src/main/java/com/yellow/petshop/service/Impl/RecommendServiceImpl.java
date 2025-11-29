package com.yellow.petshop.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.model.home.Commodity;
import com.yellow.petshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    private CommodityMapper commodityMapper;
    public List<Commodity> getRecommendCommodityList(){
        return commodityMapper.selectList(null);
    }
}
