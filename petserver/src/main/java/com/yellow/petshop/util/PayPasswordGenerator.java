package com.yellow.petshop.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 支付密码生成工具
 * 用于生成 BCrypt 加密的支付密码
 */
public class PayPasswordGenerator {
    public static void main(String[] args) {
        // 测试支付密码列表（6位数字）
        String[] passwords = {"123456", "888888", "000000", "111111"};
        
        System.out.println("=== 支付密码 BCrypt 生成工具 ===\n");
        
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
        
        // 为用户ID=1生成支付密码 123456
        String hash123456 = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println("-- 设置用户 ID=1 的支付密码为 '123456'");
        System.out.println("UPDATE user_wallet SET pay_password = '" + hash123456 + "' WHERE user_id = 1;");
        System.out.println();
        
        System.out.println("提示：");
        System.out.println("1. 每次运行生成的哈希值都不同（因为盐值随机），但都可以验证成功");
        System.out.println("2. BCrypt 哈希值格式：$2a$10$...(共60个字符)");
        System.out.println("3. 数据库字段类型必须是 VARCHAR(255) 或更大");
        System.out.println("4. 如果出现 'for input string: 2a' 错误，检查：");
        System.out.println("   - 数据库中 pay_password 字段是否为 VARCHAR 类型");
        System.out.println("   - 是否有代码错误地将密码当作数字解析");
        System.out.println("   - MyBatis 映射是否正确");
    }
}
