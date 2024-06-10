package com.sporollan.product_service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ProductCreate {
    private String name;
    private String site;
    private String tracked;
    private Long price;
    private String img;
}
