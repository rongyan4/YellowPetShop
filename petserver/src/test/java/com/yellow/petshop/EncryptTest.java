package com.yellow.petshop;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptTest { // 类名建议首字母大写，符合Java规范

    // 定义成本因子（推荐10-12）
    private static final int COST_FACTOR = 10;

    public static void main(String[] args) {
        // 1. 定义明文密码（rawPassword需要先赋值）
        String rawPassword = "123456"; // 示例密码

        // 2. 正确调用BCrypt的哈希方法
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST_FACTOR));

        // 打印结果验证
        System.out.println("原始密码：" + rawPassword);
        System.out.println("哈希后的密码：" + hashedPassword);
    }
}