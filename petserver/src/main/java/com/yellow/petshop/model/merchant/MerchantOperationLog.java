package com.yellow.petshop.model.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家操作日志实体类
 */
@Data
@TableName("merchant_operation_log")
public class MerchantOperationLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long merchantId;
    
    private String operationType;
    
    private String operationDesc;
    
    private LocalDateTime operationTime;
    
    private String ipAddress;
}
