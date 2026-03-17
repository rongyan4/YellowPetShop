package com.yellow.petshop.controller;

import com.yellow.petshop.exception.UnauthorizedException;
import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller 基类
 * <p>封装从请求中提取当前登录用户 ID 的公共逻辑：
 * <ol>
 *   <li>优先读取由 {@code CustomerJwtInterceptor} 写入的 {@code request.setAttribute("userId")}，
 *       避免重复解析 JWT。</li>
 *   <li>若拦截器未经过（如部分非拦截路径），则降级解析 Authorization 请求头。</li>
 * </ol>
 * Token 缺失或无效时抛出 {@link UnauthorizedException}，
 * 由 {@code GlobalExceptionHandler} 统一返回 401 响应。</p>
 */
public abstract class BaseController {

    /**
     * 获取当前登录用户的 ID。
     *
     * @param request 当前 HTTP 请求
     * @return 用户 ID（非 null）
     * @throws UnauthorizedException Token 缺失、格式错误或解析失败时抛出
     */
    protected Long getUserId(HttpServletRequest request) {
        // 优先从拦截器写入的 attribute 中获取（已经过签名验证）
        Object attrUserId = request.getAttribute("userId");
        if (attrUserId instanceof Long) {
            return (Long) attrUserId;
        }
        if (attrUserId != null) {
            try {
                return Long.parseLong(attrUserId.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        // 降级：直接解析 Authorization 请求头
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("未登录");
        }
        Long userId = JwtUtil.getUserIdFromToken(header.substring(7));
        if (userId == null) {
            throw new UnauthorizedException("token 无效");
        }
        return userId;
    }
}
