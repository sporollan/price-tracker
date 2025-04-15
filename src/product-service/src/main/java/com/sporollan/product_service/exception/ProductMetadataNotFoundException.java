package com.sporollan.product_service.exception;

public class ProductMetadataNotFoundException extends RuntimeException{
    public ProductMetadataNotFoundException(String id) {
        super ("Could not find Product Metadata " + id);
    }
}