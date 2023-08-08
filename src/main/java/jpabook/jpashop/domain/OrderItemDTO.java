package jpabook.jpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long orderId;
    private String itemName;
    private Integer orderPrice;
    private Integer count;

    public OrderItemDTO(Long orderId, String itemName, Integer orderPrice, Integer count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    public OrderItemDTO(OrderItem orderItem) {
        this.itemName = orderItem.getItem().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
