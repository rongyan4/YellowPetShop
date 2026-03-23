package com.yellow.petshop.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cookie 工具类
 * 统一管理 HttpOnly Cookie 的创建与清除，支持从配置文件读取 Secure 属性。
 * 通过手动拼接 Set-Cookie 响应头支持 SameSite=Lax（Servlet API 不直接支持）。
 *
 * 开发环境 (application-dev.yml)：cookie.secure=false
 * 生产环境 (application-prod.yml)：cookie.secure=true
 */
@Component
public class CookieUtil {

    /** Cookie 有效期：7天（秒） */
    private static final int MAX_AGE = 7 * 24 * 3600;

    /**
     * 是否启用 Secure 属性，由 application-{profile}.yml 注入
     * 默认 false（兜底开发环境）
     */
    @Value("${cookie.secure:false}")
    private boolean secure;

    /**
     * 写入 HttpOnly Cookie
     *
     * @param response HTTP 响应对象
     * @param name     Cookie 名称
     * @param value    Cookie 值（token 字符串）
     */
    public void addCookie(HttpServletResponse response, String name, String value) {
        response.addHeader("Set-Cookie", buildCookieHeader(name, value, MAX_AGE));
    }

    /**
     * 清除 HttpOnly Cookie（将 Max-Age 设为 0）
     *
     * @param response HTTP 响应对象
     * @param name     Cookie 名称
     */
    public void removeCookie(HttpServletResponse response, String name) {
        response.addHeader("Set-Cookie", buildCookieHeader(name, "", 0));
    }

    /**
     * 构建 Set-Cookie 响应头字符串
     *
     * @param name   Cookie 名称
     * @param value  Cookie 值
     * @param maxAge 有效期（秒），0 表示立即删除
     * @return 完整的 Set-Cookie 头字符串
     */
    private String buildCookieHeader(String name, String value, int maxAge) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value).append("; ");
        sb.append("Max-Age=").append(maxAge).append("; ");
        sb.append("Path=/; ");
        sb.append("HttpOnly; ");
        sb.append("SameSite=Lax");
        if (secure) {
            sb.append("; Secure");
        }
        return sb.toString();
    }
}
