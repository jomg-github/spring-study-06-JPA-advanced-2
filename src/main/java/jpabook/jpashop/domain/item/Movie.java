package jpabook.jpashop.domain.item;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "MOVIE")
public class Movie extends Item {
    @Column(name = "DIRECTOR")
    private String director;

    @Column(name = "ACTOR")
    private String actor;
}
