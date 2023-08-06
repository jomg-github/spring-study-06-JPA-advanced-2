package jpabook.jpashop.domain.item;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "BOOK")
public class Book extends Item {
    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "ISBN")
    private String isbn;
}
