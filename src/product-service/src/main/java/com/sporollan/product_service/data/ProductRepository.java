package com.sporollan.product_service.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sporollan.product_service.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductMetadataId(String productMetadataId);
}