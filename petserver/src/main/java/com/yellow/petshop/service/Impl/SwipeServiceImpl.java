package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.SwipeMapper;
import com.yellow.petshop.model.home.Swipe;
import com.yellow.petshop.service.SwipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwipeServiceImpl implements SwipeService {
    private String localAddress = "";
    @Autowired
    private SwipeMapper swipeMapper;
    @Override
    public List<String> getSwipeList() {
        List<Swipe> swipeList = swipeMapper.selectList(null);

        return swipeList.stream()
                        .map(Swipe -> localAddress + Swipe.getImageUrl())  // 提取 imageUrl 字段
                        .filter(imageUrl -> imageUrl != null && !imageUrl.isEmpty())   // 可选：过滤空值
                .collect(Collectors.toList());  // 转为 List<String>
    }
}
