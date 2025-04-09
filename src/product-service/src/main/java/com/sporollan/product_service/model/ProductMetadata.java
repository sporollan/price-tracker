package com.sporollan.product_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_metadata")
@Getter @Setter
public class ProductMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    private String id;
    
    private String name;
    
    @ElementCollection
    @CollectionTable(name = "product_metadata_tracked", joinColumns = @JoinColumn(name = "product_metadata_id"))
    @Column(name = "tracked")
    private Set<String> tracked;
    
    private Long dateAdded;
    private String img;

    @OneToMany(mappedBy = "productMetadata", cascade = CascadeType.ALL)
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