package jpabook.jpashop.domain;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;

    public OrderSearch(String memberName) {
        this.memberName = memberName;
    }

    public OrderSearch(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
