package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.model.comment.CreateCommentDTO;
import com.yellow.petshop.service.CommentService;
import com.yellow.petshop.util.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页获取商品评论列表
     * 访问路径: GET /api/comments/page?commodityId=商品ID&current=1&size=10
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
     */
    @GetMapping("/count")
    public Result<Long> getCommentCount(@RequestParam Long commodityId) {
        Long count = commentService.getCommentCount(commodityId);
        return Result.success(count);
    }

    /**
     * 创建评论
     * 访问路径: POST /api/comments/create
     */
    @PostMapping("/create")
    public Result<String> createComment(@RequestBody CreateCommentDTO dto, HttpServletRequest request) {
        Long userId = getUserId(request);
        commentService.createComment(userId, dto);
        return Result.success("评论成功");
    }

    /**
     * 上传评论图片
     * 访问路径: POST /api/comments/upload_image
     */
    @PostMapping("/upload_image")
    public Result<Map<String, Object>> uploadCommentImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        FileUploadUtil.UploadResult result = FileUploadUtil.uploadFile(
                file,
                FileUploadUtil.BusinessType.COMMENT_IMAGE,
                userId
        );
        if (!result.isSuccess()) {
            return Result.error(result.getMessage());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", result.getImageUrl());
        data.put("fileName", result.getFileName());
        data.put("fileSize", result.getFileSize());
        data.put("uploadTime", result.getUploadTime());
        return Result.success(data);
    }

    /**
     * 获取订单商品的评论状态
     * 访问路径: GET /api/comments/order_status?orderId=订单ID
     */
    @GetMapping("/order_status")
    public Result<List<Map<String, Object>>> getOrderCommentStatus(
            @RequestParam Long orderId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Map<String, Object>> statusList = commentService.getOrderCommentStatus(orderId, userId);
        return Result.success(statusList);
    }
}
