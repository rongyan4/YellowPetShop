package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.CommentImageMapper;
import com.yellow.petshop.mapper.CommentMapper;
import com.yellow.petshop.mapper.OrderMapper;
import com.yellow.petshop.mapper.OrderItemMapper;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.comment.Comment;
import com.yellow.petshop.model.comment.CommentImage;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.model.comment.CreateCommentDTO;
import com.yellow.petshop.model.order.Order;
import com.yellow.petshop.model.order.OrderItem;
import com.yellow.petshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
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
    
    @Override
    @Transactional
    public void createComment(Long userId, CreateCommentDTO dto) {
        // 验证订单（如果提供了订单ID）
        if (dto.getOrderId() != null) {
            Order order = orderMapper.selectById(dto.getOrderId());
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("无权评论此订单");
            }
            if (!"COMPLETED".equalsIgnoreCase(order.getStatus())) {
                throw new RuntimeException("只能评论已完成的订单");
            }
            
            // 检查是否已评论（针对订单商品）
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id", dto.getOrderId());
            wrapper.eq("commodity_id", dto.getCommodityId());
            wrapper.eq("user_id", userId);
            Long count = commentMapper.selectCount(wrapper);
            if (count > 0) {
                throw new RuntimeException("已评论过该商品");
            }
        }
        
        // 创建评论
        Comment comment = new Comment();
        comment.setCommodityId(dto.getCommodityId());
        comment.setUserId(userId);
        comment.setOrderId(dto.getOrderId());  // 可以为 null
        comment.setParentId(dto.getParentId());
        comment.setStar(dto.getStar());
        comment.setContent(dto.getContent());
        comment.setStatus("normal");
        comment.setIsTop(false);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.insert(comment);
        
        // 保存评论图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (int i = 0; i < dto.getImages().size(); i++) {
                CommentImage image = new CommentImage();
                image.setCommentId(comment.getId());
                image.setImageUrl(dto.getImages().get(i));
                image.setSortOrder(i);
                image.setCreateTime(LocalDateTime.now());
                commentImageMapper.insert(image);
            }
        }
        
        // 如果是回复评论，更新父评论的回复数
        if (dto.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment != null) {
                parentComment.setReplyCount(parentComment.getReplyCount() + 1);
                commentMapper.updateById(parentComment);
            }
        }
    }
    
    @Override
    public List<Map<String, Object>> getOrderCommentStatus(Long orderId, Long userId) {
        // 获取订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }
        
        // 获取订单商品列表
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        
        // 查询每个商品的评论状态
        List<Map<String, Object>> statusList = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Map<String, Object> status = new HashMap<>();
            status.put("commodityId", item.getCommodityId());
            
            // 查询是否已评论
            QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
            commentWrapper.eq("order_id", orderId);
            commentWrapper.eq("commodity_id", item.getCommodityId());
            commentWrapper.eq("user_id", userId);
            Comment comment = commentMapper.selectOne(commentWrapper);
            
            if (comment != null) {
                // 已评论，返回评论信息
                status.put("commented", true);
                status.put("commentId", comment.getId());
                status.put("star", comment.getStar());
                status.put("content", comment.getContent());
                status.put("createTime", comment.getCreateTime());
                
                // 查询评论图片
                QueryWrapper<CommentImage> imageWrapper = new QueryWrapper<>();
                imageWrapper.eq("comment_id", comment.getId());
                imageWrapper.orderByAsc("sort_order");
                List<CommentImage> images = commentImageMapper.selectList(imageWrapper);
                List<String> imageUrls = images.stream()
                        .map(CommentImage::getImageUrl)
                        .collect(Collectors.toList());
                status.put("images", imageUrls);
            } else {
                // 未评论
                status.put("commented", false);
            }
            
            statusList.add(status);
        }
        
        return statusList;
    }
}
