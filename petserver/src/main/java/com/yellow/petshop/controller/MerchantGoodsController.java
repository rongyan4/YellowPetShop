package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.MerchantGoodsService;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 商家商品管理控制器
 */
@RestController
@RequestMapping("/api/merchant/goods")
public class MerchantGoodsController {

    @Autowired
    private MerchantGoodsService goodsService;
    
    @Value("${file.upload.path:src/main/resources/static/images/goods}")
    private String uploadPath;

    /**
     * 上传商品图片
     */
    @PostMapping("/upload-image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 验证文件大小（限制5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小不能超过5MB");
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            Path filePath = Paths.get(uploadPath, filename);
            Files.write(filePath, file.getBytes());
            
            // 返回访问URL
            String imageUrl = "/images/goods/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("filename", filename);
            
            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    public Result<PageResult<CommodityInfo>> getGoodsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            PageResult<CommodityInfo> result = goodsService.getGoodsList(page, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加商品
     */
    @PostMapping("/add")
    public Result<String> addGoods(@RequestBody CommodityInfo commodity, HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            goodsService.addGoods(commodity, merchantId, ipAddress);
            return Result.success("商品添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    public Result<String> updateGoods(@RequestBody CommodityInfo commodity, HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            goodsService.updateGoods(commodity, merchantId, ipAddress);
            return Result.success("商品更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteGoods(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            goodsService.deleteGoods(id, merchantId, ipAddress);
            return Result.success("商品删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除商品
     */
    @DeleteMapping("/batch-delete")
    public Result<String> batchDeleteGoods(@RequestBody List<Long> ids, HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            goodsService.batchDeleteGoods(ids, merchantId, ipAddress);
            return Result.success("商品批量删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从Token中获取商家ID
     */
    private Long getMerchantIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return JwtUtil.getUserIdFromToken(token);
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
