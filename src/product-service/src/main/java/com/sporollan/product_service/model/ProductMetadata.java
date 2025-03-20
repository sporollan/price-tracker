package com.sporollan.product_service.model;

import java.time.Instant;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection="productMetadata")
@Getter @Setter
public class ProductMetadata {
    private @Id String id;
       
    private String name;
    private Set<String> tracked;
    private Long dateAdded;
    private String img;

    @DBRef
    private List<Product> products;

    public ProductMetadata() {
        this.dateAdded = Instant.now().toEpochMilli();
    }
    public ProductMetadata(String name, Set<String> tracked, String img) {
        this.name = name;
        this.tracked = tracked;
        this.img = img;
        this.dateAdded = Instant.now().toEpochMilli();
    }
}