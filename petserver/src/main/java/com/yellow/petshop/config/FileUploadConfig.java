package com.yellow.petshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件上传配置类
 * 配置文件上传路径和访问映射
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.base-dir}")
    private String baseDir;
    
    @Value("${file.upload.url-prefix}")
    private String urlPrefix;
    
    /**
     * 配置静态资源映射
     * 将 base-dir 目录映射到 url-prefix 路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 判断是相对路径还是绝对路径
        String locationPath;
        if (baseDir.startsWith("/") || baseDir.contains(":")) {
            // 绝对路径（Linux: /var/www/... 或 Windows: C:/...）
            locationPath = "file:" + baseDir + "/";
        } else {
            // 相对路径（如：src/main/resources/static/images）
            locationPath = "file:" + System.getProperty("user.dir") + "/" + baseDir + "/";
        }
        
        // 映射 url-prefix 到 base-dir
        registry.addResourceHandler(urlPrefix + "/**")
                .addResourceLocations(locationPath);
        
        System.out.println("=== 文件上传配置 ===");
        System.out.println("URL前缀: " + urlPrefix);
        System.out.println("物理路径: " + locationPath);
        System.out.println("==================");
    }
    
    /**
     * 获取文件上传根目录
     */
    public String getBaseDir() {
        return baseDir;
    }
    
    /**
     * 获取文件访问URL前缀
     */
    public String getUrlPrefix() {
        return urlPrefix;
    }
    
    /**
     * 获取相对路径URL（不包含域名）
     * @param businessPath 业务路径，如：goods/goods_1_20260224120000_1234.jpg
     * @return 相对路径URL，如：/api/images/goods/goods_1_20260224120000_1234.jpg
     */
    public String getRelativeUrl(String businessPath) {
        if (businessPath == null || businessPath.isEmpty()) {
            return null;
        }
        
        // 如果已经包含urlPrefix，直接返回
        if (businessPath.startsWith(urlPrefix)) {
            return businessPath;
        }
        
        // 移除开头的斜杠（如果有）
        if (businessPath.startsWith("/")) {
            businessPath = businessPath.substring(1);
        }
        
        // 拼接相对路径URL：url-prefix + / + businessPath
        return urlPrefix + "/" + businessPath;
    }
}
