package com.sporollan.product_service.web;

import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.data.ProductRepository;
import com.sporollan.product_service.model.Product;
import com.sporollan.product_service.model.ProductCreate;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.exception.ProductNotFoundException;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class ProductService {
    private final ProductRepository repo;
    private final ProductMetadataRepository repoMetadata;

    public ProductService(ProductRepository repo, ProductMetadataRepository repoMetadata) {
        this.repo = repo;
        this.repoMetadata = repoMetadata;
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return repo.findAll();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable String id) {
        return repo.findById(id)
                   .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @PostMapping("/product")
    public ProductCreate createProduct(@RequestBody ProductCreate entity) {
        // looks for metadata and creates it if not found
        repoMetadata.findById(entity.getName())
            .orElse(repoMetadata
                .save(new ProductMetadata(
                            entity.getName(),
                            entity.getTracked(),
                            entity.getImg()
                            )));

        repo.save(new Product(
                            entity.getName(),
                            entity.getSite(),
                            entity.getPrice()
                            ));
        return entity;
    }

    @PutMapping("product/{id}")
    public Product putMethodName(@PathVariable String id, @RequestBody Product entity) {
        return repo.findById(id).map(
            product -> {
                product.setProductMetadataId(entity.getProductMetadataId());
                product.setSite(entity.getSite());
                product.setPrice(entity.getPrice());

                return product;
            })
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    
}
