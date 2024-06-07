package com.sporollan.product_service.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sporollan.product_service.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
