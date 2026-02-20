package com.yellow.petshop.service;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.model.comment.MerchantReplyDTO;
import com.yellow.petshop.model.home.CommodityInfo;

import java.util.List;

/**
 * 商家商品服务接口
 */
public interface MerchantGoodsService {
    
    /**
     * 分页查询商品列表
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @return 商品列表
     */
    PageResult<CommodityInfo> getGoodsList(int page, int pageSize, String keyword);
    
    /**
     * 添加商品
     * @param commodity 商品信息
     * @param merchantId 商家ID
     * @param ipAddress IP地址
     */
    void addGoods(CommodityInfo commodity, Long merchantId, String ipAddress);
    
    /**
     * 更新商品
     * @param commodity 商品信息
     * @param merchantId 商家ID
     * @param ipAddress IP地址
     */
    void updateGoods(CommodityInfo commodity, Long merchantId, String ipAddress);
    
    /**
     * 删除商品
     * @param id 商品ID
     * @param merchantId 商家ID
     * @param ipAddress IP地址
     */
    void deleteGoods(Long id, Long merchantId, String ipAddress);
    
    /**
     * 批量删除商品
     * @param ids 商品ID列表
     * @param merchantId 商家ID
     * @param ipAddress IP地址
     */
    void batchDeleteGoods(List<Long> ids, Long merchantId, String ipAddress);
    
    /**
     * 更新商品上下架状态
     * @param productId 商品ID
     * @param isValid 是否上架
     */
    void updateProductStatus(Long productId, Boolean isValid);
    
    /**
     * 获取商品评论列表
     * @param productId 商品ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    PageResult<CommentVO> getProductComments(Long productId, Integer page, Integer size);
    
    /**
     * 商家回复评论
     * @param dto 回复DTO
     */
    void replyComment(MerchantReplyDTO dto);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     */
    void deleteComment(Long commentId);
    
    /**
     * 置顶评论
     * @param commentId 评论ID
     * @param isTop 是否置顶
     */
    void topComment(Long commentId, Boolean isTop);
}
