package com.sporollan.product_service.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super ("Could not find Product " + id);
    }
}
