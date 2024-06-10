package com.sporollan.product_service.web;

import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.exception.ProductNotFoundException;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class ProductMetadataService {
    private final ProductMetadataRepository repoMetadata;

    public ProductMetadataService(ProductMetadataRepository repoMetadata) {
        this.repoMetadata = repoMetadata;
    }

    @GetMapping("/productMetadata")
    public List<ProductMetadata> getProducts() {
        return repoMetadata.findAll();
    }

    @GetMapping("/productMetadata/{id}")
    public ProductMetadata getProduct(@PathVariable String id) {
        return repoMetadata.findById(id)
                   .orElseThrow(() -> new ProductNotFoundException(id));
    }
  
}
