package com.yellow.petshop.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新商品状态DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductStatusDTO {
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 是否上架 true-上架 false-下架
     */
    private Boolean isValid;
}
