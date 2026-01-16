package com.yellow.petshop.config;

import com.yellow.petshop.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置JWT拦截器和CORS跨域
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册拦截器
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要JWT验证的路径
                .excludePathPatterns(
                        "/api/user/login",       // 登录接口
                        "/api/user/register",    // 注册接口
                        "/swagger-ui/**",        // Swagger UI
                        "/v3/api-docs/**",       // Swagger API文档
                        "/swagger-resources/**", // Swagger资源
                        "/webjars/**",           // Swagger依赖
                        "/error",                // 错误页面
                        "/favicon.ico",          // 网站图标
                        "/api/swipe/**",         // 轮播图（公开访问）
                        "/api/recommend/**"      // 推荐商品（公开访问）
                );
    }

    /**
     * 配置CORS跨域
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的前端域名
                .allowedOrigins("http://localhost:8080", "http://localhost:8081", "http://127.0.0.1:8080")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带认证信息（如Cookie、Authorization头）
                .allowCredentials(true)
                // 预检请求的有效期（秒）
                .maxAge(3600);
    }
}
