package com.yellow.petshop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yellow.petshop.mapper.CartMapper;
import com.yellow.petshop.model.cart.CartItem;
import com.yellow.petshop.model.cart.CartItemVO;
import com.yellow.petshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Override
    public List<CartItemVO> getCartByUserId(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }
    
    @Override
    public Long addToCart(Long userId, Long commodityId, Integer quantity) {
        // 查询是否已存在该商品
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("commodity_id", commodityId);
        CartItem existingItem = cartMapper.selectOne(queryWrapper);
        
        if (existingItem != null) {
            // 如果已存在，更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartMapper.updateById(existingItem);
            return existingItem.getId();
        } else {
            // 如果不存在，新增
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setCommodityId(commodityId);
            newItem.setQuantity(quantity);
            newItem.setChecked(true);
            cartMapper.insert(newItem);
            return newItem.getId();
        }
    }
    
    @Override
    public Boolean updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(quantity);
        return cartMapper.updateById(cartItem) > 0;
    }
    
    @Override
    public Boolean updateChecked(Long cartItemId, Boolean checked) {
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setChecked(checked);
        return cartMapper.updateById(cartItem) > 0;
    }
    
    @Override
    public Boolean deleteCartItem(Long cartItemId) {
        return cartMapper.deleteById(cartItemId) > 0;
    }
    
    @Override
    public Boolean clearCart(Long userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return cartMapper.delete(queryWrapper) > 0;
    }
}
