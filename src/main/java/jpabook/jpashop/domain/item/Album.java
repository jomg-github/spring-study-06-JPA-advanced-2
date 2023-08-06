package jpabook.jpashop.domain.item;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "ALBUM")
public class Album extends Item {
    @Column(name = "ARTIST")
    private String artist;

    @Column(name = "ETC")
    private String etc;
}
