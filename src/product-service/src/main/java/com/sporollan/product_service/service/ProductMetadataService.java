package com.sporollan.product_service.service;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.model.ProductMetadata;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductMetadataService {
    private final ProductMetadataRepository repoMetadata;

    public ProductMetadataService(ProductMetadataRepository repoMetadata) {
        this.repoMetadata = repoMetadata;
    }

    public Page<ProductMetadata> getByTracked(String tracked, Pageable pageable) {
        return repoMetadata.findByTrackedContaining(tracked, pageable);
    }

}
