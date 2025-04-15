package com.sporollan.product_service.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sporollan.product_service.model.ProductMetadata;

public interface ProductMetadataRepository extends JpaRepository<ProductMetadata, String> {
    List<ProductMetadata> findByTrackedContaining(String tracked);
    ProductMetadata findByName(String name);

}
