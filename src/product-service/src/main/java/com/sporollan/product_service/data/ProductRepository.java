package com.sporollan.product_service.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.sporollan.product_service.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByProductMetadataId(@Param("productMetadataId") String productMetadataId);
}
