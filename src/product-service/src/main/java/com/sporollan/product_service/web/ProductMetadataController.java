package com.sporollan.product_service.web;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sporollan.product_service.dto.ProductMetadataDto;
import com.sporollan.product_service.exception.ProductMetadataNotFoundException;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.service.ProductMetadataService;

@RestController
@RequestMapping("/productMetadata")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductMetadataController {
    @Autowired
    private final ProductMetadataService metadataService;

    public ProductMetadataController(ProductMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping("/{tracked}")
    public ResponseEntity<List<ProductMetadataDto>> getByTracked(@PathVariable String tracked) {
        List<ProductMetadataDto> dtoList;
        try {
            dtoList = metadataService.getByTracked(tracked).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
        } catch (ProductMetadataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.emptyList()); 
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
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