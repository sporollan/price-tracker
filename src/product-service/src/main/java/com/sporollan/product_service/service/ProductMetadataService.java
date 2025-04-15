package com.sporollan.product_service.service;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.exception.ProductMetadataNotFoundException;
import com.sporollan.product_service.model.ProductMetadata;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductMetadataService {
    private final ProductMetadataRepository repoMetadata;

    public ProductMetadataService(ProductMetadataRepository repoMetadata) {
        this.repoMetadata = repoMetadata;
    }

    public List<ProductMetadata> getByTracked(String tracked) {
        List<ProductMetadata> db_p = repoMetadata.findByTrackedContaining(tracked);
        if (db_p.isEmpty()) {
            throw new ProductMetadataNotFoundException("Tracked not found");
        }
        return db_p;
    }

}
