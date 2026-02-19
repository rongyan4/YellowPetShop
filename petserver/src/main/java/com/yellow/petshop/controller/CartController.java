package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.cart.CartItemVO;
import com.yellow.petshop.service.CartService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 获取用户购物车列表
     * 访问路径: GET /api/cart/list
     * 需要登录
     */
    @GetMapping("/list")
    public Result<List<CartItemVO>> getCartList(HttpServletRequest request) {
        // 从请求头获取token并解析用户ID
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        List<CartItemVO> cartList = cartService.getCartByUserId(userId);
        return Result.success(cartList);
    }
    
    /**
     * 添加商品到购物车
     * 访问路径: POST /api/cart/add
     * 需要登录
     */
    @PostMapping("/add")
    public Result<Long> addToCart(
            @RequestParam Long commodityId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        try {
            Long cartItemId = cartService.addToCart(userId, commodityId, quantity);
            if (cartItemId != null) {
                return Result.success(cartItemId);
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新购物车商品数量
     * 访问路径: PUT /api/cart/quantity
     */
    @PutMapping("/quantity")
    public Result<String> updateQuantity(
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {
        
        Boolean success = cartService.updateQuantity(cartItemId, quantity);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 更新购物车商品选中状态
     * 访问路径: PUT /api/cart/checked
     */
    @PutMapping("/checked")
    public Result<String> updateChecked(
            @RequestParam Long cartItemId,
            @RequestParam Boolean checked) {
        
        Boolean success = cartService.updateChecked(cartItemId, checked);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 删除购物车商品
     * 访问路径: DELETE /api/cart/delete
     */
    @DeleteMapping("/delete")
    public Result<String> deleteCartItem(@RequestParam Long cartItemId) {
        Boolean success = cartService.deleteCartItem(cartItemId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 清空购物车
     * 访问路径: DELETE /api/cart/clear
     */
    @DeleteMapping("/clear")
    public Result<String> clearCart(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        
        token = token.substring(7);
        Long userId = JwtUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            return Result.error("token无效");
        }
        
        Boolean success = cartService.clearCart(userId);
        if (success) {
            return Result.success("清空成功");
        } else {
            return Result.error("清空失败");
        }
    }
}
