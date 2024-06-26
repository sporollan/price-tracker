package com.sporollan.product_service.web;

import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.model.ProductMetadata;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ProductMetadataService {
    private final ProductMetadataRepository repoMetadata;

    public ProductMetadataService(ProductMetadataRepository repoMetadata) {
        this.repoMetadata = repoMetadata;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/productMetadata")
    public List<ProductMetadata> getProducts() {
        return repoMetadata.findAll();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/productMetadata/{tracked}")
    public List<ProductMetadata> getProduct(@PathVariable String tracked) {
        List<ProductMetadata> db_p= repoMetadata.findByTracked(tracked);
        if(db_p.isEmpty()) {
            // exception
        }
        return db_p;
    }
  
}
