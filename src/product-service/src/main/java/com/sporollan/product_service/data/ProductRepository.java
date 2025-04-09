package com.sporollan.product_service.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sporollan.product_service.model.Product;
import com.sporollan.product_service.model.ProductMetadata;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductMetadataId(String productMetadataId);
    @Query("SELECT p FROM Product p WHERE p.productMetadata = :productMetadata AND p.site = :site AND p.dateAdded >= :startDate AND p.dateAdded < :endDate")
    List<Product> findByProductMetadataAndSiteAndDateAddedBetween(
        @Param("productMetadata") ProductMetadata productMetadata,
        @Param("site") String site,
        @Param("startDate") Long startDate,
        @Param("endDate") Long endDate
    );
}