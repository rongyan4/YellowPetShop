package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 分页获取商品评论列表
     * 访问路径: GET /api/comments/page?commodityId=商品ID&current=1&size=10
     *
     * @param commodityId 商品ID
     * @param current 当前页码（从1开始），默认为1
     * @param size 每页大小，默认为10
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<CommentVO>> getCommentsByPage(
            @RequestParam Long commodityId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        
        PageResult<CommentVO> pageResult = commentService.getCommentsByPage(commodityId, current, size);
        return Result.success(pageResult);
    }
    
    /**
     * 获取商品评论总数
     * 访问路径: GET /api/comments/count?commodityId=商品ID
     *
     * @param commodityId 商品ID
     * @return 评论总数
     */
    @GetMapping("/count")
    public Result<Long> getCommentCount(@RequestParam Long commodityId) {
        Long count = commentService.getCommentCount(commodityId);
        return Result.success(count);
    }
}
