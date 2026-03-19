package com.yellow.petshop.interceptor;

import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 客户端JWT拦截器
 * 用于验证客户端请求中的JWT Token，防止商家端token越权访问客户端接口
 */
@Component
public class CustomerJwtInterceptor implements HandlerInterceptor {

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

        // 从请求头中获取Token
        String authHeader = request.getHeader("Authorization");
        String token;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 标准 Authorization 头（axios 等普通请求）
            token = authHeader.substring(7);
        } else {
            // 降级：从 query 参数读取（EventSource 等无法设置请求头的场景）
            token = request.getParameter("token");
            if (token == null || token.isBlank()) {
                sendErrorResponse(response, 401, "未提供认证令牌");
                return false;
            }
        }

        try {
            // 验证Token是否有效
            if (!jwtUtil.validateToken(token)) {
                sendErrorResponse(response, 401, "认证令牌无效或已过期");
                return false;
            }

            // 验证Token类型是否为客户端
            if (!JwtUtil.isCustomerToken(token)) {
                sendErrorResponse(response, 403, "无权访问客户端接口，请使用客户账号登录");
                return false;
            }

            // 提取用户ID并存储到请求属性中，供后续Controller使用
            Long userId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractusername(token);
            String userType = jwtUtil.extractUserType(token);
            
            request.setAttribute("userId", userId);
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
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"code\":%d,\"msg\":\"%s\",\"data\":null}", status, message));
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
