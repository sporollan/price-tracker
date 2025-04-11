package com.sporollan.product_service.web;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.dto.ProductDto;
import com.sporollan.product_service.exception.DuplicateProductException;
import com.sporollan.product_service.model.ProductCreate;
import com.sporollan.product_service.service.ProductService;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    @Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/greeting")
    public String greeting() {
        return productService.greeting();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/{id}")
    public List<ProductDto> getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<List<ProductDto>> createProduct(@RequestBody List<ProductCreate> entityBatch) {
        try {
            List<ProductDto> productDtos = productService.createProduct(entityBatch);
            return ResponseEntity.status(HttpStatus.CREATED).body(productDtos);
        } catch (DuplicateProductException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        } catch (Exception e) {
            // Log the exception and return INTERNAL_SERVER_ERROR
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}