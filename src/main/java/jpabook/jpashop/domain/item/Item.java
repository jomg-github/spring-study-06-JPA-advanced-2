package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // JOINED 가장 정규화, TABLE_PER_CLASS 각각의 테이블
@DiscriminatorColumn(name = "DTYPE")
@Table(name = "TB_ITEM")
@Getter @Setter
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * 재고 증가
     */
    public void increaseStockQuantity(Integer quantity) {
        stockQuantity = stockQuantity + quantity;
    }

    /**
     * 재고 감소
     */
    public void decreaseStockQuantity(Integer quantity) {
        if (stockQuantity - quantity < 0) {
            throw new NotEnoughStockException("재고가 부족합니다.");
        } else {
            stockQuantity = stockQuantity - quantity;
        }
    }
}
