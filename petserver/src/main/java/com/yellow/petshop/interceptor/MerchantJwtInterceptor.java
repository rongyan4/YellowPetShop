package com.yellow.petshop.interceptor;

import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 商家端JWT拦截器
 * 用于验证商家端请求中的JWT Token，防止客户端token越权访问商家端接口
 */
@Component
public class MerchantJwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil = new JwtUtil();

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true表示继续流程，false表示中断
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从 Authorization 请求头读取 Access Token（2分钟有效期，存localStorage）
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isBlank()) {
            sendErrorResponse(response, 401, "未提供认证令牌");
            return false;
        }

        try {
            // 验证Token是否有效（含过期检查）
            if (!jwtUtil.validateToken(token)) {
                // AT 过期，返回 4010 让前端凭 RT 刷新
                sendErrorResponse(response, 4010, "Access Token 已过期，请刷新");
                return false;
            }

            // 验证Token类型是否为商家端
            if (!JwtUtil.isMerchantToken(token)) {
                sendErrorResponse(response, 403, "无权访问商家端接口，请使用商家账号登录");
                return false;
            }

            // 提取商家ID并存储到请求属性中，供后续Controller使用
            Long merchantId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractusername(token);
            String userType = jwtUtil.extractUserType(token);
            
            request.setAttribute("merchantId", merchantId);
            request.setAttribute("userId", merchantId); // 兼容旧代码
            request.setAttribute("username", username);
            request.setAttribute("userType", userType);
            
            // Token验证通过，继续处理请求
            return true;

        } catch (Exception e) {
            // Token解析失败
            sendErrorResponse(response, 401, "认证令牌解析失败");
            return false;
        }
    }

    /**
     * 发送错误响应
     * 4010 为业务码，表示 AT 过期需前端刷新，HTTP 状态码统一用 401
     */
    private void sendErrorResponse(HttpServletResponse response, int httpStatus, String message) throws Exception {
        int realHttpStatus = (httpStatus == 4010) ? 401 : httpStatus;
        response.setStatus(realHttpStatus);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"code\":%d,\"msg\":\"%s\",\"data\":null}", httpStatus, message));
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
                          org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // 可以在这里添加一些后处理逻辑
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet渲染了对应的视图之后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, 
                               Exception ex) throws Exception {
        // 可以在这里添加一些清理逻辑
    }
}
