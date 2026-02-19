package com.yellow.petshop.model.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 钱包信息VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletVO {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private Boolean isLocked;
    private Boolean hasPayPassword; // 是否设置了支付密码
}
