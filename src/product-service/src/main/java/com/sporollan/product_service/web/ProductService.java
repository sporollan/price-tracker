package com.sporollan.product_service.web;

import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.data.ProductRepository;
import com.sporollan.product_service.dto.ProductDto;
import com.sporollan.product_service.dto.ProductUpdateRequest;
import com.sporollan.product_service.model.Product;
import com.sporollan.product_service.model.ProductCreate;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.exception.ProductMetadataNotFoundException;
import com.sporollan.product_service.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    public void dropDB() {
        repo.deleteAll();
        repoMetadata.deleteAll();
    }
    @GetMapping("/")
    public String greeting() {
        return "Hello, World!";
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return repo.findAll();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/product/{id}")
    public List<ProductDto> getProduct(@PathVariable String id) {

        List<Product> products = repo.findByProductMetadataId(id);
        return products.stream().map(product -> new ProductDto(
                product.getId(),
                product.getProductMetadata() != null ? product.getProductMetadata().getId() : null,
                product.getSite(),
                product.getPrice(),
                product.getDateAdded()
        )).collect(Collectors.toList());
    
    }
    
    @PostMapping("/product")
    public ResponseEntity<List<ProductDto>> createProduct(@RequestBody List<ProductCreate> entityBatch) {
        // looks for metadata and creates it if not found
        List<Product> savedProducts = new ArrayList<>();
        try 
        {
            for(ProductCreate entity: entityBatch){
                ProductMetadata db_md = repoMetadata.findByName(entity.getName());
                if(db_md == null) {
                    db_md = repoMetadata
                    .save(new ProductMetadata(
                                        entity.getName(),
                                        new HashSet<>(Collections.singleton(entity.getTracked())),
                                        entity.getImg()
                                        ));
                } else {
                    // update to latest img
                    db_md.getTracked().add(entity.getTracked());
                    db_md.setImg(entity.getImg());
                    db_md = repoMetadata.save(db_md);
                }

                savedProducts.add(new Product(
                    db_md,
                    entity.getSite(),
                    entity.getPrice()
                    ));
                
            }
            List<Product> createdProducts = repo.saveAll(savedProducts);
            List<ProductDto> productDtos = createdProducts.stream().map(product -> new ProductDto(
                product.getId(),
                product.getProductMetadata() != null ? product.getProductMetadata().getId() : null,
                product.getSite(),
                product.getPrice(),
                product.getDateAdded()
            )).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(productDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PutMapping("/product/{id}")
    public Product putMethodName(@PathVariable String id, @RequestBody ProductUpdateRequest updateRequest) {
        return repo.findById(id).map(
            product -> {
                ProductMetadata metadata = repoMetadata.findById(updateRequest.getProductMetadataId())
                .orElseThrow(() -> new ProductMetadataNotFoundException(updateRequest.getProductMetadataId()));
                product.setProductMetadata(metadata);
                product.setSite(updateRequest.getSite());
                product.setPrice(updateRequest.getPrice());

                return product;
            })
            .orElseThrow(() -> new ProductNotFoundException(id));
    }    
    
}
