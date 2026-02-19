package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.browse.BrowseHistoryVO;
import com.yellow.petshop.service.BrowseHistoryService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/browse")
public class BrowseHistoryController {
    
    @Autowired
    private BrowseHistoryService browseHistoryService;
    
    /**
     * 添加浏览记录
     */
    @PostMapping("/add")
    public Result<String> addBrowseHistory(@RequestHeader("Authorization") String token,
                                           @RequestParam Long commodityId) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        boolean success = browseHistoryService.addBrowseHistory(userId, commodityId);
        
        if (success) {
            return Result.success("添加浏览记录成功");
        } else {
            return Result.error("添加浏览记录失败");
        }
    }
    
    /**
     * 获取浏览记录列表
     */
    @GetMapping("/list")
    public Result<List<BrowseHistoryVO>> getBrowseHistoryList(@RequestHeader("Authorization") String token,
                                                               @RequestParam(defaultValue = "100") int limit) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        List<BrowseHistoryVO> list = browseHistoryService.getBrowseHistoryList(userId, limit);
        
        return Result.success(list);
    }
    
    /**
     * 清空浏览记录
     */
    @DeleteMapping("/clear")
    public Result<String> clearBrowseHistory(@RequestHeader("Authorization") String token) {
        // 去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("token无效");
        }
        
        boolean success = browseHistoryService.clearBrowseHistory(userId);
        
        if (success) {
            return Result.success("清空浏览记录成功");
        } else {
            return Result.error("清空浏览记录失败");
        }
    }
}
