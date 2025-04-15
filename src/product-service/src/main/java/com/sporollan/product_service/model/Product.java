package com.sporollan.product_service.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter @Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_metadata_id")
    private ProductMetadata productMetadata;

    private String site;
    private Long price;
    private Long dateAdded;
    
    public Product() {
        this.dateAdded = Instant.now().toEpochMilli();
    }

    public Product(ProductMetadata productMetadata, String site, Long price) {
        this.dateAdded = Instant.now().toEpochMilli();
        this.productMetadata = productMetadata;
        this.site = site;
        this.price = price;
    }
}
