package com.sporollan.product_service.service;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.data.ProductRepository;
import com.sporollan.product_service.dto.ProductDto;
import com.sporollan.product_service.model.Product;
import com.sporollan.product_service.model.ProductCreate;
import com.sporollan.product_service.model.ProductMetadata;

import jakarta.transaction.Transactional;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import com.sporollan.product_service.exception.DuplicateProductException;

import org.springframework.stereotype.Service;


@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;
    private final ProductMetadataRepository repoMetadata;

    public ProductService(ProductRepository repo, ProductMetadataRepository repoMetadata) {
        this.repo = repo;
        this.repoMetadata = repoMetadata;
    }

    public String greeting() {
        return "Hello, World!";
    }

    public List<ProductDto> getProductById(String id) {

        List<Product> products = repo.findByProductMetadataId(id);
        return products.stream().map(product -> new ProductDto(
                product.getId(),
                product.getProductMetadata() != null ? product.getProductMetadata().getId() : null,
                product.getSite(),
                product.getPrice(),
                product.getDateAdded()
        )).collect(Collectors.toList());
    
    }
    
    public List<ProductDto> createProduct(List<ProductCreate> entityBatch) {
        List<Product> savedProducts = new ArrayList<>();
        boolean atLeastOneCreated = false;
        boolean atLeastOneDuplicate = false;
        try {
            for (ProductCreate entity : entityBatch) {
                ProductMetadata db_md = repoMetadata.findByName(entity.getName());
                if (db_md == null) {
                    // Create new metadata if not exists
                    db_md = repoMetadata.save(new ProductMetadata(
                        entity.getName(),
                        new HashSet<>(Collections.singleton(entity.getTracked())),
                        entity.getImg()
                    ));
                } else {
                    // Update metadata if it exists
                    db_md.getTracked().add(entity.getTracked());
                    db_md.setImg(entity.getImg());
                    db_md = repoMetadata.save(db_md);
                }
                // Check for existing entries in DB and current batch
                Instant now = Instant.now();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDate today = now.atZone(zoneId).toLocalDate();
                Instant startOfDay = today.atStartOfDay(zoneId).toInstant();
                Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);
                long startMillis = startOfDay.toEpochMilli();
                long endMillis = endOfDay.toEpochMilli();

                // Check database
                List<Product> existingInDb = repo.findByProductMetadataAndSiteAndDateAddedBetween(
                    db_md, entity.getSite(), startMillis, endMillis
                );
                if (!existingInDb.isEmpty()) {
                    atLeastOneDuplicate = true;

                    continue;
                }
                // Create a final reference for use in lambda
                final ProductMetadata finalDbMd = db_md;

                // Check current batch
                boolean existsInBatch = savedProducts.stream().anyMatch(p ->
                    p.getProductMetadata().equals(finalDbMd) && // Use final variable here
                    p.getSite().equals(entity.getSite()) &&
                    p.getDateAdded() >= startMillis &&
                    p.getDateAdded() < endMillis
                );
                if (existsInBatch) {
                    atLeastOneDuplicate = true;
                    continue;
                }
                Product newProduct = new Product(db_md, entity.getSite(), entity.getPrice());
                savedProducts.add(newProduct);
                atLeastOneCreated = true;
            }
    
            List<Product> createdProducts = repo.saveAll(savedProducts);
            List<ProductDto> productDtos = createdProducts.stream()
                .map(p -> new ProductDto(p.getId(), p.getProductMetadata().getId(), p.getSite(), p.getPrice(), p.getDateAdded()))
                .collect(Collectors.toList());
            if(!atLeastOneCreated && atLeastOneDuplicate) {
                throw new DuplicateProductException(null);
            }
            return productDtos;
            // Let Controller handle exceptions
        } catch (DuplicateProductException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
