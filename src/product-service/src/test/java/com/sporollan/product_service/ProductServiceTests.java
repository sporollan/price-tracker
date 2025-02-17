package com.sporollan.product_service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sporollan.product_service.model.Product;
import com.sporollan.product_service.model.ProductCreate;
import com.sporollan.product_service.model.ProductMetadata;
import com.sporollan.product_service.web.ProductMetadataService;
import com.sporollan.product_service.web.ProductService;

@SpringBootTest
public class ProductServiceTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMetadataService productMetadataService;

    @Test
    public void testGreeting() {
        String greeting = productService.greeting();
        assert greeting.equals("Hello, World!");
    }

    @Test
    public void testCreateProduct() {
        // drop DB before test
        productService.dropDB();
        // prepare product
        ProductCreate product = new ProductCreate();
        product.setName("Producto Cafe 100g");
        product.setSite("COTO");
        product.setTracked("Cafe");
        product.setPrice(10000L);
        // create Product, also creates metadata
        ProductCreate createResponse = productService
                                    .createProduct(product);
        // get metadata
        List<ProductMetadata> metadata = productMetadataService
                                            .getProduct("Cafe");
        ProductMetadata md = metadata.get(0);
        // assert metadata is created and product has correct id
        List<Product> productResponse = productService.getProduct(md.getId());
        Product p = productResponse.get(0);
        assert p.getProductMetadataId().equals(md.getId());
        assert md.getTracked().equals("Cafe");
        assert md.getName().equals("Producto Cafe 100g");

        assert createResponse.getSite().equals("COTO");
        assert createResponse.getPrice() == 10000L;

    }
}
