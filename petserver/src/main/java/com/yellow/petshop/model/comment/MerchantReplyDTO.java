package com.yellow.petshop.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家回复评论DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantReplyDTO {
    /**
     * 评论ID
     */
    private Long commentId;
    
    /**
     * 回复内容
     */
    private String reply;
}
