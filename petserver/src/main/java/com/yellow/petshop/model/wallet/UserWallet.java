package com.yellow.petshop.model.wallet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包实体类
 */
@TableName("user_wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWallet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String payPassword;
    private Boolean isLocked;
    private LocalDateTime lockTime;
    private Integer errorCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
