package main.java.com.petshop.controller;

import main.java.com.petshop.model.Swipe;
import main.java.com.petshop.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // 统一接口前缀
public class HomeController {
    private HomeService homeService;
    /**
     * 轮播图接口
     * 访问路径: /api/swipe
     * @return 轮播图图片路径数组
     */
    @GetMapping("/swipe")
    public List<String> getSwipeImages() {
        List<Swipe> swipeList = homeService.getSwipeList();
        // 返回预设的轮播图路径数组
        return swipeList.stream()
                .map(Swipe::getImageUrl)
                .collect(Collectors.toList());
    }
}