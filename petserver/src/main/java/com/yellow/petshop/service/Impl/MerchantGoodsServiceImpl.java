package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.CommentMapper;
import com.yellow.petshop.mapper.CommodityMapper;
import com.yellow.petshop.mapper.MerchantOperationLogMapper;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.comment.Comment;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.model.comment.MerchantReplyDTO;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.merchant.MerchantOperationLog;
import com.yellow.petshop.service.MerchantGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家商品服务实现
 */
@Service
public class MerchantGoodsServiceImpl implements MerchantGoodsService {

    @Autowired
    private CommodityMapper commodityMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private MerchantOperationLogMapper operationLogMapper;

    @Override
    public PageResult<CommodityInfo> getGoodsList(int page, int pageSize, String keyword) {
        QueryWrapper<CommodityInfo> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        wrapper.orderByDesc("id");
        
        long offset = (long) (page - 1) * pageSize;
        Long total = commodityMapper.selectCount(wrapper);
        
        wrapper.last("LIMIT " + offset + ", " + pageSize);
        List<CommodityInfo> list = commodityMapper.selectList(wrapper);
        
        PageResult<CommodityInfo> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(list);
        result.setCurrent((long) page);
        result.setSize((long) pageSize);
        result.setPages((total + pageSize - 1) / pageSize);
        
        return result;
    }

    @Override
    @Transactional
    public void addGoods(CommodityInfo commodity, Long merchantId, String ipAddress) {
        commodity.setIsValid(true);
        commodityMapper.insert(commodity);
        
        // 记录操作日志
        logOperation(merchantId, "商品管理", "添加商品：" + commodity.getName(), ipAddress);
    }

    @Override
    @Transactional
    public void updateGoods(CommodityInfo commodity, Long merchantId, String ipAddress) {
        commodityMapper.updateById(commodity);
        
        // 记录操作日志
        logOperation(merchantId, "商品管理", "更新商品：" + commodity.getName(), ipAddress);
    }

    @Override
    @Transactional
    public void deleteGoods(Long id, Long merchantId, String ipAddress) {
        CommodityInfo commodity = commodityMapper.selectById(id);
        if (commodity != null) {
            commodityMapper.deleteById(id);
            
            // 记录操作日志
            logOperation(merchantId, "商品管理", "删除商品：" + commodity.getName(), ipAddress);
        }
    }

    @Override
    @Transactional
    public void batchDeleteGoods(List<Long> ids, Long merchantId, String ipAddress) {
        for (Long id : ids) {
            commodityMapper.deleteById(id);
        }
        
        // 记录操作日志
        logOperation(merchantId, "商品管理", "批量删除商品，数量：" + ids.size(), ipAddress);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long productId, Boolean isValid) {
        CommodityInfo commodity = commodityMapper.selectById(productId);
        if (commodity == null) {
            throw new RuntimeException("商品不存在");
        }
        
        commodity.setIsValid(isValid);
        commodityMapper.updateById(commodity);
    }

    @Override
    public PageResult<CommentVO> getProductComments(Long productId, Integer page, Integer size) {
        // 查询总数
        Long total = commentMapper.selectCommentCount(productId);
        
        // 计算偏移量
        long offset = (long) (page - 1) * size;
        
        // 分页查询
        List<CommentVO> comments = commentMapper.selectCommentsByPage(productId, offset, (long) size);
        
        PageResult<CommentVO> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(comments);
        result.setCurrent(page.longValue());
        result.setSize(size.longValue());
        result.setPages((total + size - 1) / size);
        
        return result;
    }

    @Override
    @Transactional
    public void replyComment(MerchantReplyDTO dto) {
        Comment comment = commentMapper.selectById(dto.getCommentId());
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        comment.setMerchantReply(dto.getReply());
        comment.setMerchantReplyTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        comment.setStatus("deleted");
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional
    public void topComment(Long commentId, Boolean isTop) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        comment.setIsTop(isTop);
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.updateById(comment);
    }
    
    /**
     * 记录操作日志
     */
    private void logOperation(Long merchantId, String operationType, String operationDesc, String ipAddress) {
        MerchantOperationLog log = new MerchantOperationLog();
        log.setMerchantId(merchantId);
        log.setOperationType(operationType);
        log.setOperationDesc(operationDesc);
        log.setIpAddress(ipAddress);
        log.setOperationTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
