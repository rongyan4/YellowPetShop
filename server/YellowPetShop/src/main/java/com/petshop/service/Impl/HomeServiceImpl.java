package main.java.com.petshop.service.Impl;

import main.java.com.petshop.mapper.SwipeMapper;
import main.java.com.petshop.model.Swipe;
import main.java.com.petshop.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private SwipeMapper swipeMapper;
    @Override
    public List<Swipe> getSwipeList() {
        return swipeMapper.selectList(null);
    }
}
