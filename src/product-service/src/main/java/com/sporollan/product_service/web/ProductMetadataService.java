package com.sporollan.product_service.web;

import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.dto.ProductMetadataDto;
import com.sporollan.product_service.model.ProductMetadata;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<ProductMetadataDto> getProducts() {
        return repoMetadata.findAll().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/productMetadata/{tracked}")
    public ResponseEntity<List<ProductMetadataDto>> getProduct(@PathVariable String tracked) {
        List<ProductMetadata> db_p = repoMetadata.findByTrackedContaining(tracked);
        if (db_p.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.emptyList());
        }

        List<ProductMetadataDto> dtoList = db_p.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(dtoList);
    }
    private ProductMetadataDto mapToDto(ProductMetadata metadata) {
        return new ProductMetadataDto(
            metadata.getId(),
            metadata.getName(),
            metadata.getTracked(),
            metadata.getDateAdded(),
            metadata.getImg()
        );
    }
}
