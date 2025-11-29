package com.yellow.petshop.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.model.home.Commodity;
import com.yellow.petshop.model.home.Swipe;
import com.yellow.petshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private ObjectMapper objectMapper;
    private List<String> list;
    public List<Commodity> getRecommendCommodityList(){
        List<Commodity> commodityList = commodityMapper.selectList(null);
        return commodityList;
    }
}
