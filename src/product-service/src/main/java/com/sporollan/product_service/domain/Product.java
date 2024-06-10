package com.sporollan.product_service.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Product {
    private @Id String id;

    private String name;
    private String site;
    private String tracked;
    private Long dateAdded;

    private String[] imgPath;

    public Product() {
        this.dateAdded = Instant.now().toEpochMilli();
    }
}
