package com.yellow.petshop.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {
    //加密密码
    public static String encrypt(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean verify(String rawPassword, String encryptedPassword) {
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }
}