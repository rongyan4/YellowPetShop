package com.yellow.petshop.model.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家登录日志实体类
 */
@Data
@TableName("merchant_login_log")
public class MerchantLoginLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long merchantId;
    
    private LocalDateTime loginTime;
    
    private String ipAddress;
    
    private String userAgent;
    
    private Integer loginStatus;
}
