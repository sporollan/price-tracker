package com.sporollan.product_service.web;

import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.data.ProductRepository;
import com.sporollan.product_service.domain.Product;
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

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return repo.findAll();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @PostMapping("/product")
    public Product createProduct(@RequestBody Product entity) {        
        return repo.save(entity);
    }

    @PutMapping("product/{id}")
    public Product putMethodName(@PathVariable Long id, @RequestBody Product entity) {
        return repo.findById(id).map(
            product -> {
                product.setName(entity.getName());
                product.setSite(entity.getSite());
                return product;
            })
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    
}
