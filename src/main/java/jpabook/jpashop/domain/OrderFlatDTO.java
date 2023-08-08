package jpabook.jpashop.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderFlatDTO {
    private Long orderId;
    private String memberName;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    private String itemName;
    private Integer orderPrice;
    private Integer count;

    public OrderFlatDTO(Long orderId, String memberName, LocalDateTime orderDate, OrderStatus orderStatus, Address address, String itemName, Integer orderPrice, Integer count) {
        this.orderId = orderId;
        this.memberName = memberName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
