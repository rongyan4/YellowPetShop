package com.yellow.petshop.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码生成工具
 * 用于生成 BCrypt 加密的密码，方便更新数据库中的密码
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        // 测试密码列表
        String[] passwords = {"123456", "admin123", "test123"};
        
        System.out.println("=== BCrypt 密码生成工具 ===\n");
        
        for (String password : passwords) {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("原始密码: " + password);
            System.out.println("加密后:   " + hashed);
            
            // 验证加密是否正确
            boolean match = BCrypt.checkpw(password, hashed);
            System.out.println("验证结果: " + (match ? "✓ 成功" : "✗ 失败"));
            System.out.println();
        }
        
        System.out.println("=== SQL 更新语句示例 ===\n");
        System.out.println("-- 将用户 ID=1 的密码更新为 '123456'");
        String hash123456 = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println("UPDATE user SET password = '" + hash123456 + "' WHERE id = 1;");
        System.out.println();
        System.out.println("提示：每次运行生成的哈希值都不同（因为盐值随机），但都可以验证成功");
    }
}
