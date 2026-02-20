package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.model.comment.CreateCommentDTO;
import com.yellow.petshop.service.CommentService;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    
    /**
     * 创建评论
     * 访问路径: POST /api/comments/create
     *
     * @param dto 评论DTO
     * @param request HTTP请求
     * @return 结果
     */
    @PostMapping("/create")
    public Result<String> createComment(@RequestBody CreateCommentDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            commentService.createComment(userId, dto);
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.getUserIdFromToken(token);
    }
}
