package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ORDER_ITEM")
@Getter @Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @Column(name = "ORDER_PRICE")
    private Integer orderPrice;

    @Column(name = "COUNT")
    private Integer count;

    /**
     * 주문 상품 엔티티 생성 메소드
     */
    public static OrderItem createOrderItem(Item item, Integer orderPrice, Integer count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.decreaseStockQuantity(count);

        return orderItem;
    }

    /**
     * 주문 취소 후, 재고 원복
     */
    public void cancel() {
        getItem().increaseStockQuantity(count);
    }

    /**
     * 주문 상품 가격 * 주문 상품 개수
     */
    public Integer getTotalPrice() {
        return orderPrice * count;
    }
}
