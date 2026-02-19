package com.yellow.petshop.config;

import com.yellow.petshop.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置JWT拦截器、CORS跨域和静态资源映射
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
                // 只拦截用户相关的请求
                .addPathPatterns("/api/user/**")
                // 排除登录和注册接口
                .excludePathPatterns(
                        "/api/user/login",      // 登录接口
                        "/api/user/register"    // 注册接口
                );
    }

    /**
     * 配置CORS跨域
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源（开发环境）
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带认证信息（如Cookie、Authorization头）
                .allowCredentials(true)
                // 预检请求的有效期（秒）
                .maxAge(3600);
    }

    /**
     * 配置静态资源映射
     * 将 /api/images/** 映射到 classpath:/static/images/
     * 将 /comment_image/** 映射到 classpath:/public/comment_image/
     * 使用 /api/images 避免与前端 public/images 冲突
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射头像等图片资源
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        // 映射评论图片资源
        registry.addResourceHandler("/comment_image/**")
                .addResourceLocations("classpath:/public/comment_image/");
    }
}
