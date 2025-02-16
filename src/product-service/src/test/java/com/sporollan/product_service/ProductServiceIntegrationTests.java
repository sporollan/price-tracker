package com.sporollan.product_service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.equalTo;


public class ProductServiceIntegrationTests {

    private static final String SERVICE_URL =
        System.getProperty("service.url", "http://localhost:8080");

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = SERVICE_URL;
    }

    @Test
    public void testGreeting() {
        RestAssured
            .given()
                .get("/")
            .then()
                .statusCode(200)
                .body(equalTo("Hello, World"));
    }
}
