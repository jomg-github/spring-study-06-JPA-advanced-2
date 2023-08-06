package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Test
    void 상품등록() {
        // given
        Book book = new Book();
        book.setName("해리포터");
        book.setPrice(10000);
        book.setStockQuantity(35);

        // when
        Long newBookId = itemService.regist(book);
        Item newBook = itemService.findOne(newBookId);

        // then
        assertThat(newBookId).isEqualTo(book.getId());
    }

    @Test
    void 상품재고차감_에러_테스트() {
        // given
        Book book = new Book();
        book.setName("해리포터");
        book.setPrice(10000);
        book.setStockQuantity(35);

        // when
        itemService.regist(book);
        book.decreaseStockQuantity(35);

        // then
        assertThat(book.getStockQuantity()).isEqualTo(0);
        assertThrows(NotEnoughStockException.class, () -> book.decreaseStockQuantity(1));
    }
}