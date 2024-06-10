package com.sporollan.product_service.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection="products")
@Getter @Setter
public class Product {
    private @Id String id;
    private String productMetadataId;
    private String site;
    private Long price;
    private Long dateAdded;


    public Product() {
        this.dateAdded = Instant.now().toEpochMilli();
    }
    public Product(String productMetadataId, String site, Long price) {
        this.dateAdded = Instant.now().toEpochMilli();
        this.productMetadataId = productMetadataId;
        this.site = site;
        this.price = price;
    }
}
