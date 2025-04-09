package com.sporollan.product_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ProductDto {
    private String id;
    private String productMetadataId;
    private String site;
    private Long price;
    private Long dateAdded;

    // Constructors, getters, and setters
    public ProductDto(String id, String productMetadataId, String site, Long price, Long dateAdded) {
        this.id = id;
        this.productMetadataId = productMetadataId;
        this.site = site;
        this.price = price;
        this.dateAdded = dateAdded;
    }

}
