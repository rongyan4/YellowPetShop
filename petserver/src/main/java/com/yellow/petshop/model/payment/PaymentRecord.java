package com.yellow.petshop.model.payment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单支付记录实体类
 */
@TableName("payment_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long userId;
    private String paymentMethod; // WALLET, WECHAT, ALIPAY
    private BigDecimal amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String transactionNo;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
