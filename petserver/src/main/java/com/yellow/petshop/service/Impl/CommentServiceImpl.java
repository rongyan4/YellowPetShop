package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.CommentImageMapper;
import com.yellow.petshop.mapper.CommentMapper;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.comment.CommentImage;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentImageMapper commentImageMapper;
    
    @Override
    public PageResult<CommentVO> getCommentsByPage(Long commodityId, Long current, Long size) {
        // 计算偏移量
        Long offset = (current - 1) * size;
        
        // 查询评论列表
        List<CommentVO> comments = commentMapper.selectCommentsByPage(commodityId, offset, size);
        
        // 如果有评论，查询评论图片
        if (!comments.isEmpty()) {
            List<Long> commentIds = comments.stream()
                    .map(CommentVO::getId)
                    .collect(Collectors.toList());
            
            // 批量查询评论图片
            List<CommentImage> images = commentImageMapper.selectByCommentIds(commentIds);
            
            // 按评论ID分组
            Map<Long, List<String>> imageMap = images.stream()
                    .collect(Collectors.groupingBy(
                            CommentImage::getCommentId,
                            Collectors.mapping(CommentImage::getImageUrl, Collectors.toList())
                    ));
            
            // 设置每个评论的图片列表
            comments.forEach(comment -> {
                comment.setImages(imageMap.getOrDefault(comment.getId(), List.of()));
            });
        }
        
        // 查询总记录数
        Long total = commentMapper.selectCommentCount(commodityId);
        
        // 计算总页数
        Long pages = (total + size - 1) / size;
        
        // 判断是否有下一页
        Boolean hasNext = current < pages;
        
        return PageResult.<CommentVO>builder()
                .current(current)
                .size(size)
                .total(total)
                .pages(pages)
                .records(comments)
                .hasNext(hasNext)
                .build();
    }
    
    @Override
    public Long getCommentCount(Long commodityId) {
        return commentMapper.selectCommentCount(commodityId);
    }
}
