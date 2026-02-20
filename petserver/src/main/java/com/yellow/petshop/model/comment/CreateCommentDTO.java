package com.yellow.petshop.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建评论DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDTO {
    /**
     * 商品ID
     */
    private Long commodityId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 父评论ID（回复评论时使用）
     */
    private Long parentId;
    
    /**
     * 评分（1-5星）
     */
    private Integer star;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 评论图片URL列表
     */
    private List<String> images;
}
