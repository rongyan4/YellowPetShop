package com.yellow.petshop.interceptor;

import com.yellow.petshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 用于验证请求中的JWT Token
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

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
        
        // 检查Authorization头是否存在
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未提供认证令牌\",\"data\":null}");
            return false;
        }

        // 提取Token（去掉"Bearer "前缀）
        String token = authHeader.substring(7);

        try {
            // 验证Token是否有效
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"认证令牌无效或已过期\",\"data\":null}");
                return false;
            }

            // 提取用户ID并存储到请求属性中，供后续Controller使用
            Long userId = jwtUtil.extractUserId(token);
            request.setAttribute("userId", userId);
            
            // Token验证通过，继续处理请求
            return true;

        } catch (Exception e) {
            // Token解析失败
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"认证令牌解析失败\",\"data\":null}");
            return false;
        }
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
