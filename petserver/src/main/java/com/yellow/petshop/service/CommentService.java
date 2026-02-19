package com.yellow.petshop.service;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.comment.CommentVO;

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
}
