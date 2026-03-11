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
 * 钱包交易记录实体类
 */
@TableName("wallet_transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransaction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    /** 交易类型：RECHARGE-充值, WITHDRAW-提现, DEDUCT-扣款, ADD-增加 */
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String remark;
    private LocalDateTime createTime;
}
