package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @Test
    void 상품주문() {
        // given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order order = orderRepository.findById(orderId);

        // then
        assertThat(order.getStatus()).as("주문 상태는 ORDER").isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).as("주문 상품 종류 수").isEqualTo(1);
        assertThat(book.getPrice() * orderCount).as("주문 가격은 상품 가격 * 수량").isEqualTo(order.getTotalPrice());
        assertThat(8).as("주문 수량만큼 재고 차감").isEqualTo(book.getStockQuantity());
    }

    @Test
    void 주문취소() {
        // given
        Member member = createMember();
        Book book = createBook();
        int oldStock = book.getStockQuantity();
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancel(orderId);

        // then
        Order order = orderRepository.findById(orderId);

        assertThat(order.getStatus()).as("주문 상태는 CANCEL").isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).as("재고는 원복").isEqualTo(oldStock);
    }

    @Test
    void 상품주문_실패_재고수량초과() {
        // given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 11;

        // when
        // then
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));
    }

    @Test
    void 주문검색() {
        // given
        Member 조민기 = createMember("조민기");
        Member 이민기 = createMember("이민기");
        Member 손흥민 = createMember("손흥민");
        Member 손웅정 = createMember("손웅정");
        Member 해리케인 = createMember("해리케인");

        Book book = createBook();

        // 주문
        orderService.order(조민기.getId(), book.getId(), 1);
        orderService.order(이민기.getId(), book.getId(), 2);
        orderService.order(손흥민.getId(), book.getId(), 3);
        orderService.order(손웅정.getId(), book.getId(), 2);

        // 주문취소
        Long orderId = orderService.order(해리케인.getId(), book.getId(), 1);
        orderService.cancel(orderId);


        // when
        // then
        assertThat(1).isEqualTo(orderService.search(new OrderSearch("조민기")).size());
        assertThat(2).isEqualTo(orderService.search(new OrderSearch("민기")).size());
        assertThat(3).isEqualTo(orderService.search(new OrderSearch("민")).size());

        assertThat(4).isEqualTo(orderService.search(new OrderSearch(OrderStatus.ORDER)).size());
        assertThat(1).isEqualTo(orderService.search(new OrderSearch(OrderStatus.CANCEL)).size());

        assertThat(1).isEqualTo(orderService.search(new OrderSearch("조민기", OrderStatus.ORDER)).size());
        assertThat(0).isEqualTo(orderService.search(new OrderSearch("조민기", OrderStatus.CANCEL)).size());

        assertThat(0).isEqualTo(orderService.search(new OrderSearch("해리", OrderStatus.ORDER)).size());
        assertThat(1).isEqualTo(orderService.search(new OrderSearch("케인", OrderStatus.CANCEL)).size());
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("BOOK1");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("조민기");
        member.setAddress(new Address("경기도", "수원시", "123-123"));
        em.persist(member);
        return member;
    }

    private Member createMember(String memberName) {
        Member member = new Member();
        member.setName(memberName);
        member.setAddress(new Address("경기도", "수원시", "123-123"));
        em.persist(member);
        return member;
    }

}