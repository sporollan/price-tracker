package com.sporollan.product_service.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Product {
    private @Id @GeneratedValue Long id;

    private String name;
    private String site;
    private Long dateAdded;

    public Product() {
        this.dateAdded = Instant.now().toEpochMilli();
    }
}
