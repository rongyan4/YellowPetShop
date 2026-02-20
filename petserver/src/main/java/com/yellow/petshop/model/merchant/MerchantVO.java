package com.yellow.petshop.model.merchant;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家信息VO
 */
@Data
public class MerchantVO {
    
    private Long id;
    
    private String username;
    
    private String merchantName;
    
    private String contactPerson;
    
    private String contactPhone;
    
    private String email;
    
    private Integer status;
    
    private LocalDateTime lastLoginTime;
}
