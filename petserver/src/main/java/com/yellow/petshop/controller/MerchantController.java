package com.yellow.petshop.controller;

import com.yellow.petshop.model.Result;
import com.yellow.petshop.model.merchant.MerchantLoginRequest;
import com.yellow.petshop.model.merchant.MerchantVO;
import com.yellow.petshop.model.merchant.DashboardVO;
import com.yellow.petshop.model.merchant.OrderDetailVO;
import com.yellow.petshop.model.merchant.ShipOrderDTO;
import com.yellow.petshop.model.merchant.UpdateOrderPriceDTO;
import com.yellow.petshop.model.comment.MerchantReplyDTO;
import com.yellow.petshop.model.PageResult;
import com.yellow.petshop.model.order.OrderVO;
import com.yellow.petshop.model.comment.CommentVO;
import com.yellow.petshop.service.MerchantService;
import com.yellow.petshop.service.MerchantOrderService;
import com.yellow.petshop.service.MerchantGoodsService;
import com.yellow.petshop.service.RefreshTokenStore;
import com.yellow.petshop.model.token.RefreshToken;
import com.yellow.petshop.util.CookieUtil;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    
    @Autowired
    private MerchantOrderService merchantOrderService;
    
    @Autowired
    private MerchantGoodsService merchantGoodsService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private RefreshTokenStore refreshTokenStore;

    /** RT 有效期：7天（毫秒） */
    private static final long RT_TTL_MS = 1000L * 60 * 60 * 24 * 7;

    /**
     * 商家登录
     */
    @PostMapping("/login")
    public Result<java.util.Map<String, Object>> login(@RequestBody MerchantLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            String ipAddress = getIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            // login() 内部验证账密，返回 RT 明文
            String refreshToken = merchantService.login(
                    request.getUsername(),
                    request.getPassword(),
                    ipAddress,
                    userAgent
            );
            Long merchantId = JwtUtil.getUserIdFromToken(refreshToken);

            // 持久化 RT（有状态，支持服务端主动吊销）
            refreshTokenStore.save(refreshToken, merchantId, "merchant", request.getUsername(), RT_TTL_MS);

            // 生成 AT（2分钟）
            String accessToken = JwtUtil.generateMerchantAccessToken(merchantId, request.getUsername());

            // RT 写入 HttpOnly Cookie
            cookieUtil.addCookie(httpResponse, "merchant_token", refreshToken);
            // AT 返回给前端存 localStorage
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("accessToken", accessToken);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 刷新商家 Access Token
     * 前端 AT 过期后，凭 Cookie 中的 RT（merchant_token）换取新 AT
     * 访问路径: POST /api/merchant/refresh
     */
    @PostMapping("/refresh")
    public Result<java.util.Map<String, Object>> refresh(HttpServletRequest httpRequest) {
        // 从 Cookie 读取 RT
        String refreshToken = null;
        jakarta.servlet.http.Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("merchant_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            return Result.error("未提供 Refresh Token，请重新登录");
        }
        try {
            // 有状态校验：查库验证 RT 存在 + 未吊销 + 未过期
            RefreshToken stored = refreshTokenStore.validate(refreshToken);
            if (stored == null) {
                return Result.error("Refresh Token 无效或已过期，请重新登录");
            }
            if (!"merchant".equals(stored.getUserType())) {
                return Result.error("Token 类型错误");
            }
            // 生成新 AT（RT 不轮换）
            String accessToken = JwtUtil.generateMerchantAccessToken(stored.getUserId(), stored.getUsername());
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("accessToken", accessToken);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 商家退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // 从 Cookie 读取 RT 并吊销（有状态登出）
        jakarta.servlet.http.Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("merchant_token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    try { refreshTokenStore.revoke(cookie.getValue()); } catch (Exception ignored) {}
                    break;
                }
            }
        }
        // 清除 HttpOnly Cookie
        cookieUtil.removeCookie(httpResponse, "merchant_token");
        return Result.success("退出成功");
    }

    /**
     * 获取当前商家信息
     */
    @GetMapping("/info")
    public Result<MerchantVO> getMerchantInfo(HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            MerchantVO merchantVO = merchantService.getMerchantById(merchantId);
            return Result.success(merchantVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<String> updatePassword(@RequestBody UpdatePasswordRequest request, HttpServletRequest httpRequest) {
        try {
            Long merchantId = getMerchantIdFromToken(httpRequest);
            merchantService.updatePassword(merchantId, request.getOldPassword(), request.getNewPassword());
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取数据概览
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        try {
            DashboardVO dashboard = merchantOrderService.getDashboard();
            return Result.success(dashboard);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询订单列表
     */
    @GetMapping("/orders")
    public Result<PageResult<OrderVO>> getOrderList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<OrderVO> result = merchantOrderService.getOrderList(status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/orders/{orderId}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        try {
            OrderDetailVO detail = merchantOrderService.getOrderDetail(orderId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改待支付订单价格
     */
    @PostMapping("/orders/update-price")
    public Result<String> updateOrderPrice(@RequestBody UpdateOrderPriceDTO dto) {
        try {
            merchantOrderService.updateOrderPrice(dto);
            return Result.success("订单价格修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 发货
     */
    @PostMapping("/orders/ship")
    public Result<String> shipOrder(@RequestBody ShipOrderDTO dto) {
        try {
            merchantOrderService.shipOrder(dto);
            return Result.success("发货成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记订单为已完成
     */
    @PostMapping("/orders/{orderId}/complete")
    public Result<String> completeOrder(@PathVariable Long orderId) {
        try {
            merchantOrderService.completeOrder(orderId);
            return Result.success("订单已完成");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新商品上下架状态
     */
    @PostMapping("/products/{productId}/status")
    public Result<String> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam Boolean isValid) {
        try {
            merchantGoodsService.updateProductStatus(productId, isValid);
            return Result.success(isValid ? "商品已上架" : "商品已下架");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取商品评论列表
     */
    @GetMapping("/products/{productId}/comments")
    public Result<PageResult<CommentVO>> getProductComments(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<CommentVO> result = merchantGoodsService.getProductComments(productId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 商家回复评论
     */
    @PostMapping("/comments/reply")
    public Result<String> replyComment(@RequestBody MerchantReplyDTO dto) {
        try {
            merchantGoodsService.replyComment(dto);
            return Result.success("回复成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId) {
        try {
            merchantGoodsService.deleteComment(commentId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 置顶评论
     */
    @PostMapping("/comments/{commentId}/top")
    public Result<String> topComment(
            @PathVariable Long commentId,
            @RequestParam Boolean isTop) {
        try {
            merchantGoodsService.topComment(commentId, isTop);
            return Result.success(isTop ? "置顶成功" : "取消置顶成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询商品列表
     */
    @GetMapping("/products")
    public Result<PageResult<com.yellow.petshop.model.home.CommodityInfo>> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            PageResult<com.yellow.petshop.model.home.CommodityInfo> result = 
                merchantGoodsService.getGoodsList(page, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加商品
     */
    @PostMapping("/products")
    public Result<String> addProduct(
            @RequestBody com.yellow.petshop.model.home.CommodityInfo commodity,
            HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            merchantGoodsService.addGoods(commodity, merchantId, ipAddress);
            return Result.success("商品添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/products/{productId}")
    public Result<String> updateProduct(
            @PathVariable Long productId,
            @RequestBody com.yellow.petshop.model.home.CommodityInfo commodity,
            HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            commodity.setId(productId);
            merchantGoodsService.updateGoods(commodity, merchantId, ipAddress);
            return Result.success("商品更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/products/{productId}")
    public Result<String> deleteProduct(
            @PathVariable Long productId,
            HttpServletRequest request) {
        try {
            Long merchantId = getMerchantIdFromToken(request);
            String ipAddress = getIpAddress(request);
            merchantGoodsService.deleteGoods(productId, merchantId, ipAddress);
            return Result.success("商品删除成功");
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
        // 降级：从 HttpOnly Cookie 中手动提取
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("merchant_token".equals(cookie.getName())) {
                    return JwtUtil.getUserIdFromToken(cookie.getValue());
                }
            }
        }
        // 最终降级：从 Authorization 请求头读取（兼容非浏览器客户端）
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

    /**
     * 修改密码请求DTO
     */
    public static class UpdatePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
