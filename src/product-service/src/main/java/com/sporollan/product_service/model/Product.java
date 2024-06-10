package com.sporollan.product_service.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection="products")
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
