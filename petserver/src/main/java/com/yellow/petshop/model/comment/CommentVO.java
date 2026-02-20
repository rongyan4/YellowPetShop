package com.yellow.petshop.model.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象（包含用户信息和图片）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private Long id;
    private Long commodityId;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Long orderId;
    private Long parentId;
    private Integer star;
    private String content;
    private String status;
    private Boolean isTop;
    private Integer likeCount;
    private Integer replyCount;
    private String merchantReply;
    private LocalDateTime merchantReplyTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> images;
}
