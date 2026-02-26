package com.yellow.petshop.service;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.model.comment.CreateCommentDTO;

import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 分页获取商品评论列表
     * @param commodityId 商品ID
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<CommentVO> getCommentsByPage(Long commodityId, Long current, Long size);
    
    /**
     * 获取商品评论总数
     * @param commodityId 商品ID
     * @return 评论总数
     */
    Long getCommentCount(Long commodityId);
    
    /**
     * 创建评论
     * @param userId 用户ID
     * @param dto 评论DTO
     */
    void createComment(Long userId, CreateCommentDTO dto);
    
    /**
     * 获取订单商品的评论状态
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 评论状态列表
     */
    List<Map<String, Object>> getOrderCommentStatus(Long orderId, Long userId);
}
