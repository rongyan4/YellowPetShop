package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.address.UserAddress;
import com.yellow.petshop.service.AddressService;
import com.yellow.petshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 获取用户所有地址
     */
    @GetMapping("/list")
    public Result<List<UserAddress>> getAddressList(HttpServletRequest request) {
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
            List<UserAddress> addresses = addressService.getUserAddresses(userId);
            return Result.success(addresses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public Result<UserAddress> getDefaultAddress(HttpServletRequest request) {
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
            UserAddress address = addressService.getDefaultAddress(userId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加地址
     */
    @PostMapping("/add")
    public Result<String> addAddress(
            @RequestBody UserAddress address,
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
            Boolean success = addressService.addAddress(userId, address);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 设置默认地址
     */
    @PutMapping("/setDefault/{addressId}")
    public Result<String> setDefaultAddress(
            @PathVariable Long addressId,
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
            Boolean success = addressService.setDefaultAddress(userId, addressId);
            if (success) {
                return Result.success("设置成功");
            } else {
                return Result.error("设置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新地址
     */
    @PutMapping("/update/{addressId}")
    public Result<String> updateAddress(
            @PathVariable Long addressId,
            @RequestBody UserAddress address,
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
            address.setId(addressId);
            address.setUserId(userId);
            Boolean success = addressService.updateAddress(address);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除地址
     */
    @DeleteMapping("/delete/{addressId}")
    public Result<String> deleteAddress(
            @PathVariable Long addressId,
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
            Boolean success = addressService.deleteAddress(userId, addressId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
