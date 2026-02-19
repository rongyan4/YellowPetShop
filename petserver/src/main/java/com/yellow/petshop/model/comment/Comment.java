package com.yellow.petshop.model.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@TableName("comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long commodityId;
    private Long userId;
    private Long orderId;
    private Long parentId;
    private Integer star;
    private String content;
    private String status;
    private Boolean isTop;
    private Integer likeCount;
    private Integer replyCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
