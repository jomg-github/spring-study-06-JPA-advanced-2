package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DummyDataConfig {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class InitService {

        private final EntityManager em;

        public void dbInit() {
            Book book1 = createBook("BOOK A", 18000, 1000);
            Book book2 = createBook("BOOK B", 20000, 5000);

            Member member1 = createMember("MEMBER A", new Address("경기도", "광교중앙로 145", "00001"));
            Member member2 = createMember("MEMBER B", new Address("경기도", "미사강변중앙로 190", "00002"));

            // member1
            createOrder(member1, book1, book2);

            // member2
            createOrder(member2, book2);
        }

        private void createOrder(Member member, Item... items) {
            for (Item item : items) {
                Integer count = (int) (Math.random() * (100 - 10) + 1) * 1;
                OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
                Delivery delivery = createDelivery(member.getAddress());
                Order order = Order.createOrder(member, delivery, orderItem);
                em.persist(order);
            }

        }

        private Delivery createDelivery(Address address) {
            Delivery delivery = new Delivery();
            delivery.setAddress(address);
            delivery.setStatus(DeliveryStatus.READY);
            return delivery;
        }

        private Book createBook(String name, Integer price, Integer quantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(quantity);
            em.persist(book);

            return book;
        }

        private Member createMember(String name, Address address) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(address);
            em.persist(member);

            return member;
        }
    }
}
