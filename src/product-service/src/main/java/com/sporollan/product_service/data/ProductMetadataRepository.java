package com.sporollan.product_service.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import com.sporollan.product_service.model.ProductMetadata;

public interface ProductMetadataRepository extends MongoRepository<ProductMetadata, String> {
    List<ProductMetadata> findByTracked(@Param("tracked") String tracked);
    List<ProductMetadata> findByName(@Param("name") String name);

}
