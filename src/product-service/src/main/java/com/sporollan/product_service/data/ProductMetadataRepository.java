package com.sporollan.product_service.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sporollan.product_service.model.ProductMetadata;

public interface ProductMetadataRepository extends JpaRepository<ProductMetadata, String> {
    ProductMetadata findByName(String name);
    Page<ProductMetadata> findByTrackedContaining(String tracked, Pageable pageable);

}
