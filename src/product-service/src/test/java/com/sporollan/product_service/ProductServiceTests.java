package com.sporollan.product_service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.sporollan.product_service.data.ProductMetadataRepository;
import com.sporollan.product_service.data.ProductRepository;
import com.sporollan.product_service.dto.ProductDto;
import com.sporollan.product_service.dto.ProductMetadataDto;
import com.sporollan.product_service.model.Product;
import com.sporollan.product_service.model.ProductCreate;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.web.ProductMetadataService;
import com.sporollan.product_service.web.ProductService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class ProductServiceTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMetadataService productMetadataService;
    private final ProductRepository repo;
    private final ProductMetadataRepository repoMetadata;
    
    @Autowired
    public ProductServiceTests(ProductRepository repo, ProductMetadataRepository repoMetadata) {
        this.repo = repo;
        this.repoMetadata = repoMetadata;
    }
    @Test
    public void testGreeting() {
        String greeting = productService.greeting();
        assert greeting.equals("Hello, World!");
    }

    @Test
    public void testCreateProduct() {
        // Create Products.
        // Then assert they've been created along their metadata

        // prepare product
        ProductCreate product = new ProductCreate();
        product.setName("Producto Cafe 100g");
        product.setSite("COTO");
        product.setTracked("Cafe");
        product.setPrice(10000L);

        ProductCreate product2 = new ProductCreate();
        product2.setName("Producto Cafe 200g");
        product2.setSite("COTO");
        product2.setTracked("Cafe");
        product2.setPrice(20000L);

        ProductCreate product3 = new ProductCreate();
        product3.setName("Producto Cafe 100g");
        product3.setSite("COTO");
        product3.setTracked("Cafe Grano");
        product3.setPrice(10000L);

        List<ProductCreate> products = new ArrayList<ProductCreate>();
        products.add(product);
        products.add(product2);
        // create Product, also creates metadata
        ResponseEntity<List<ProductDto>> createResponse = productService
                                    .createProduct(products);
        // get metadata
        ResponseEntity<List<ProductMetadataDto>> metadataResponse = productMetadataService
                                            .getProduct("Cafe");
        assert metadataResponse.getStatusCode().equals(HttpStatus.OK);

        // assert metadata is created and product has correct id
        List<ProductMetadataDto> metadata = metadataResponse.getBody();
        ProductMetadataDto md = metadata.get(0);
        List<ProductDto> productResponse = productService.getProduct(md.getId());
        ProductDto p = productResponse.get(0);
        assert p.getProductMetadataId().equals(md.getId());
        assert md.getTracked().equals(new HashSet<>(Collections.singleton("Cafe")));
        assert md.getName().equals("Producto Cafe 100g");
        assert createResponse.getStatusCode().equals(HttpStatus.CREATED);
        assert createResponse.getBody().size() == 2;

        // Try adding the same product under different tracked name
        List<ProductCreate> products2 = new ArrayList<ProductCreate>();
        products2.add(product3);
        ResponseEntity<List<ProductDto>> createResponse2 = productService
                                    .createProduct(products2);
        assert createResponse2.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert createResponse2.getBody().size() == 0;
        ResponseEntity<List<ProductMetadataDto>> metadataResponse2 = productMetadataService
                                            .getProduct("Cafe Grano");
        // Tracked should link to product even if it was duplicate
        assert metadataResponse2.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    public void testCreateDuplicateProductFail(){
        
        List<ProductCreate> products = new ArrayList<ProductCreate>();
        ProductCreate product = new ProductCreate();
        product.setName("Producto Cafe 100g");
        product.setSite("COTO");
        product.setTracked("Cafe");
        product.setPrice(10000L);
        products.add(product);
        ResponseEntity<List<ProductDto>> createResponse = productService.createProduct(products);
        assert createResponse.getStatusCode().equals(HttpStatus.CREATED);

        List<ProductCreate> products2 = new ArrayList<ProductCreate>();
        ProductCreate product2 = new ProductCreate();
        product2.setName("Producto Cafe 100g");
        product2.setSite("COTO");
        product2.setTracked("Cafe");
        product2.setPrice(10000L);
        products2.add(product2);
        ResponseEntity<List<ProductDto>> createResponse2 = productService.createProduct(products2);
        assert createResponse2.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void testCreateDuplicateProductSuccess(){
        // Create same product on different days
        ProductMetadata metadata = new ProductMetadata("Producto Cafe 100g", new HashSet<>(Collections.singleton("Cafe")), "product.jpg");
        metadata = repoMetadata.save(metadata);

        // Insert Product with same name but different dateAdded
        Product product = new Product();
        product.setDateAdded(111L);
        product.setPrice(10000L);
        product.setProductMetadata(metadata);
        product.setSite("COTO");
        this.repo.save(product);

        List<ProductCreate> products2 = new ArrayList<ProductCreate>();
        ProductCreate product2 = new ProductCreate();
        product2.setName("Producto Cafe 100g");
        product2.setSite("COTO");
        product2.setTracked("Cafe");
        product2.setPrice(10000L);
        products2.add(product2);

        ResponseEntity<List<ProductDto>> createResponse2 = productService.createProduct(products2);
        assert createResponse2.getStatusCode().equals(HttpStatus.CREATED);
    }
}
