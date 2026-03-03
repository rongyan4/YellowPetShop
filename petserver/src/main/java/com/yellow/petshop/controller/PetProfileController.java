package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.model.pet.PetProfileDTO;
import com.yellow.petshop.model.pet.PetProfileVO;
import com.yellow.petshop.service.PetProfileService;
import com.yellow.petshop.util.JwtUtil;
import com.yellow.petshop.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 宠物档案控制器
 */
@RestController
@RequestMapping("/api/pet-profile")
public class PetProfileController {
    
    @Autowired
    private PetProfileService petProfileService;
    
    /**
     * 获取当前用户的宠物档案列表
     */
    @GetMapping("/list")
    public Result<List<PetProfileVO>> getList(HttpServletRequest request) {
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
            List<PetProfileVO> list = petProfileService.getList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取档案详情
     */
    @GetMapping("/{id}")
    public Result<PetProfileVO> getDetail(@PathVariable Long id, HttpServletRequest request) {
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
            PetProfileVO detail = petProfileService.getDetail(id, userId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加档案
     */
    @PostMapping("/add")
    public Result<Long> add(@RequestBody PetProfileDTO dto, HttpServletRequest request) {
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
            Long id = petProfileService.add(dto, userId);
            return Result.success(id);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新档案
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody PetProfileDTO dto, HttpServletRequest request) {
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
            petProfileService.update(dto, userId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除档案
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
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
            petProfileService.delete(id, userId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传宠物头像
     */
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 使用统一的文件上传工具类
            FileUploadUtil.UploadResult result = FileUploadUtil.uploadFile(
                file, 
                FileUploadUtil.BusinessType.USER_AVATAR,  // 复用用户头像的配置
                null  // 宠物头像不需要业务ID
            );
            
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            // 返回访问URL
            return Result.success(result.getImageUrl());
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据档案推荐商品
     */
    @GetMapping("/recommend/{petId}")
    public Result<List<CommodityInfo>> getRecommendGoods(@PathVariable Long petId, HttpServletRequest request) {
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
            List<CommodityInfo> goods = petProfileService.getRecommendGoods(petId, userId);
            return Result.success(goods);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
