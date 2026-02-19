package com.yellow.petshop.model.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论图片实体类
 */
@TableName("comment_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long commentId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
