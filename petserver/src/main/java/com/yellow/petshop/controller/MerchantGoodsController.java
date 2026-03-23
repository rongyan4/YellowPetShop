package com.yellow.petshop.controller;

import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.home.CommodityInfo;
import com.yellow.petshop.service.MerchantGoodsService;
import com.yellow.petshop.util.FileUploadUtil;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家商品管理控制器
 */
@RestController
@RequestMapping("/api/merchant/goods")
public class MerchantGoodsController {

    @Autowired
    private MerchantGoodsService goodsService;

    /**
     * 上传商品图片
     */
    @PostMapping("/upload-image")
    public Result<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            // 获取商家ID（可选，用于文件命名）
            Long merchantId = getMerchantIdFromToken(request);
            
            // 使用统一的文件上传工具
            FileUploadUtil.UploadResult result = FileUploadUtil.uploadFile(
                file, 
                FileUploadUtil.BusinessType.GOODS_IMAGE, 
                merchantId
            );
            
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            // 返回结果
            Map<String, String> data = new HashMap<>();
            data.put("url", result.getImageUrl());
            data.put("filename", result.getFileName());
            
            return Result.success(data);
        } catch (Exception e) {
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
     * 从请求属性中获取商家ID（由 MerchantJwtInterceptor 注入）
     */
    private Long getMerchantIdFromToken(HttpServletRequest request) {
        // 优先使用拦截器注入的属性（HttpOnly Cookie 认证后写入）
        Object merchantIdAttr = request.getAttribute("merchantId");
        if (merchantIdAttr != null) {
            return Long.valueOf(merchantIdAttr.toString());
        }
        // 降级：从 Authorization 请求头读取（兼容非浏览器客户端）
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
