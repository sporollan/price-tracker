package com.sporollan.product_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductUpdateRequest {

    @NotNull(message = "Product metadata ID cannot be null")
    private String productMetadataId;

    @NotNull(message = "Site cannot be null")
    private String site;

    @Positive(message = "Price must be positive")
    private Long price;

    // Getters and setters
    public String getProductMetadataId() {
        return productMetadataId;
    }

    public void setProductMetadataId(String productMetadataId) {
        this.productMetadataId = productMetadataId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}

