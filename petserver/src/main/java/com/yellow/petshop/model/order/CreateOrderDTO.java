package com.yellow.petshop.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {
    private List<OrderItemDTO> items; // 订单商品列表
    private String receiverName; // 收货人姓名
    private String receiverPhone; // 收货人电话
    private String receiverAddress; // 收货地址
    private String remark; // 订单备注
    
    @Override
    public String toString() {
        return "CreateOrderDTO{" +
                "items=" + (items == null ? "null" : "[size=" + items.size() + ", content=" + items + "]") +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
    
    /**
     * 订单商品DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO {
        private Long commodityId; // 商品ID
        private Integer quantity; // 购买数量
        private Long cartItemId; // 购物车项ID（可选，用于从购物车下单后删除）
    }
}
