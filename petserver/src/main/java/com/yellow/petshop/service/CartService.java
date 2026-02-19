package com.yellow.petshop.service;

import com.yellow.petshop.model.cart.CartItem;
import com.yellow.petshop.model.cart.CartItemVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartItemVO> getCartByUserId(Long userId);
    
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param commodityId 商品ID
     * @param quantity 数量
     * @return 购物车项ID
     */
    Long addToCart(Long userId, Long commodityId, Integer quantity);

    /**
     * 更新购物车商品数量
     * @param cartItemId 购物车项ID
     * @param quantity 新数量
     * @return 是否成功
     */
    Boolean updateQuantity(Long cartItemId, Integer quantity);
    
    /**
     * 更新购物车商品选中状态
     * @param cartItemId 购物车项ID
     * @param checked 是否选中
     * @return 是否成功
     */
    Boolean updateChecked(Long cartItemId, Boolean checked);
    
    /**
     * 删除购物车商品
     * @param cartItemId 购物车项ID
     * @return 是否成功
     */
    Boolean deleteCartItem(Long cartItemId);
    
    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean clearCart(Long userId);
}
