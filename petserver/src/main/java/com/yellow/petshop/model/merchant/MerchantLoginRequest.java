package com.yellow.petshop.model.merchant;

import lombok.Data;

/**
 * 商家登录请求DTO
 */
@Data
public class MerchantLoginRequest {
    
    private String username;
    
    private String password;
}
