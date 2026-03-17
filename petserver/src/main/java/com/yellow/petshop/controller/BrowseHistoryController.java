package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.browse.BrowseHistoryVO;
import com.yellow.petshop.service.BrowseHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/browse")
public class BrowseHistoryController extends BaseController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    /**
     * 添加浏览记录
     */
    @PostMapping("/add")
    public Result<String> addBrowseHistory(HttpServletRequest request,
                                           @RequestParam Long commodityId) {
        Long userId = getUserId(request);
        boolean success = browseHistoryService.addBrowseHistory(userId, commodityId);
        return success ? Result.success("添加浏览记录成功") : Result.error("添加浏览记录失败");
    }

    /**
     * 获取浏览记录列表
     */
    @GetMapping("/list")
    public Result<List<BrowseHistoryVO>> getBrowseHistoryList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "100") int limit) {
        Long userId = getUserId(request);
        List<BrowseHistoryVO> list = browseHistoryService.getBrowseHistoryList(userId, limit);
        return Result.success(list);
    }

    /**
     * 清空浏览记录
     */
    @DeleteMapping("/clear")
    public Result<String> clearBrowseHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        boolean success = browseHistoryService.clearBrowseHistory(userId);
        return success ? Result.success("清空浏览记录成功") : Result.error("清空浏览记录失败");
    }
}
