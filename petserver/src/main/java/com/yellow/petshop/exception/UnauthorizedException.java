package com.yellow.petshop.exception;

/**
 * 未授权异常（Token 缺失或无效）
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("未登录或 token 无效");
    }
}
