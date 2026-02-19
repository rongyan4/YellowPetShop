package com.yellow.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yellow.petshop.model.comment.Comment;
import com.yellow.petshop.model.comment.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论Mapper
 */
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 分页查询商品评论（包含用户信息）
     * @param commodityId 商品ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 评论列表
     */
    @Select("SELECT c.*, u.username, u.nickname, u.avatar " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.commodity_id = #{commodityId} AND c.status = 'normal' " +
            "ORDER BY c.is_top DESC, c.create_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<CommentVO> selectCommentsByPage(@Param("commodityId") Long commodityId, 
                                         @Param("offset") Long offset, 
                                         @Param("size") Long size);
    
    /**
     * 查询商品评论总数
     * @param commodityId 商品ID
     * @return 评论总数
     */
    @Select("SELECT COUNT(*) FROM comment WHERE commodity_id = #{commodityId} AND status = 'normal'")
    Long selectCommentCount(@Param("commodityId") Long commodityId);
}
