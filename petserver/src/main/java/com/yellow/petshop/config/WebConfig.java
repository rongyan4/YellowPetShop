package com.yellow.petshop.config;

import com.yellow.petshop.interceptor.CustomerJwtInterceptor;
import com.yellow.petshop.interceptor.MerchantJwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置JWT拦截器、CORS跨域和静态资源映射
 * 分离商家端和客户端的认证拦截，防止越权操作
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CustomerJwtInterceptor customerJwtInterceptor;
    
    @Autowired
    private MerchantJwtInterceptor merchantJwtInterceptor;

    /**
     * 注册拦截器
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ========== 商家端拦截器 ==========
        // 拦截所有商家端接口，验证商家端token
        registry.addInterceptor(merchantJwtInterceptor)
                .addPathPatterns("/api/merchant/**")
                .excludePathPatterns(
                        "/api/merchant/login",           // 商家登录接口
                        "/api/merchant/register",         // 商家注册接口（如果有）
                        "/api/user/login",               // 客户登录接口
                        "/api/user/register"             // 客户注册接口
                )
                .order(1); // 优先级1
        
        // ========== 客户端拦截器 ==========
        // 只放行显示声明的接口，验证客户端token
        registry.addInterceptor(customerJwtInterceptor)
//                .addPathPatterns(
//                        "/api/user/info",                // 获取用户信息
//                        "/api/user/update_info",         // 更新用户信息
//                        "/api/user/upload_avatar",       // 上传头像
//                        "/api/address/**",               // 地址管理
//                        "/api/cart/**",                  // 购物车
//                        "/api/order/**",                 // 订单管理
//                        "/api/favorite/**",              // 收藏
//                        "/api/comment/**",               // 评论
//                        "/api/browse/**",                // 浏览历史
//                        "/api/pet/**",                   // 宠物档案
//                        "/api/payment/**"                // 支付相关
//                )
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",               // 客户登录接口
                        "/api/user/register",             // 客户注册接口
                        "/api/goods/**",                    //(商品浏览)
                        "/api/category/**",               //(分类浏览)
                        "/api/swipe/**",                  //(轮播图)
                        "/api/recommend/**",              //(推荐)
                        "/api/search/**",                  //(搜索)
                        "/api/images/**"                   //静态资源
                )
                .order(2); // 优先级2
        
        // 公共接口不需要拦截：
        // - /api/goods/** (商品浏览)
        // - /api/category/** (分类浏览)
        // - /api/swipe/** (轮播图)
        // - /api/recommend/** (推荐)
        // - /api/search/** (搜索)
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
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 映射头像等图片资源
//        registry.addResourceHandler("/api/images/**")
//                .addResourceLocations("classpath:/static/images/");
//
//        // 映射评论图片资源
//        registry.addResourceHandler("/comment_image/**")
//                .addResourceLocations("classpath:/public/comment_image/");
//    }
}
