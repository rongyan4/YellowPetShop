package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.comment.CommentImage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论图片Mapper
 */
public interface CommentImageMapper extends BaseMapper<CommentImage> {
    
    /**
     * 根据评论ID查询图片列表
     * @param commentId 评论ID
     * @return 图片列表
     */
    @Select("SELECT * FROM comment_image WHERE comment_id = #{commentId} ORDER BY sort_order ASC")
    List<CommentImage> selectByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 批量查询评论图片
     * @param commentIds 评论ID列表
     * @return 图片列表
     */
    @Select("<script>" +
            "SELECT * FROM comment_image WHERE comment_id IN " +
            "<foreach collection='commentIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " ORDER BY comment_id, sort_order ASC" +
            "</script>")
    List<CommentImage> selectByCommentIds(@Param("commentIds") List<Long> commentIds);
}
