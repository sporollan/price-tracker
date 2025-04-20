package com.sporollan.product_service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.dto.ProductMetadataDto;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.service.ProductMetadataService;

@RestController
@RequestMapping("/productMetadata")
public class ProductMetadataController {
    @Autowired
    private final ProductMetadataService metadataService;

    public ProductMetadataController(ProductMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping("/{tracked}")
    public ResponseEntity<Page<ProductMetadataDto>> getByTracked(
            @PathVariable String tracked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductMetadataDto> dtoPage = metadataService
            .getByTracked(tracked, pageable)
            .map(this::mapToDto);

        return ResponseEntity.ok(dtoPage);
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